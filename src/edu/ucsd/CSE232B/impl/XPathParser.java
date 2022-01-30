package edu.ucsd.CSE232B.impl;

import edu.ucsd.CSE232B.parsers.ExpressionGrammarBaseVisitor;
import edu.ucsd.CSE232B.parsers.ExpressionGrammarParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class XPathParser extends ExpressionGrammarBaseVisitor<ArrayList<Node>> {
    private Document xmlDocument;
    private Node currentNode;
    ArrayList<Node> currNodes = new ArrayList<>();

    public Document getXmlDocument() {
        return xmlDocument;
    }

    public void setXmlDocument(Document xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    public String parseStr(String str){
        String result = str.substring(1, str.length()-1);
        char quote = str.charAt(0);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<result.length(); i++){
            if(result.charAt(i)=='\\' && i+1<result.length()){
                if(quote=='\'' && result.charAt(i+1)=='\"'){
                    continue;
                }else if(quote=='\"' && result.charAt(i+1)=='\''){
                    continue;
                }
            }
            stringBuilder.append(result.charAt(i));
        }

        return stringBuilder.toString();
    }

    @Override public ArrayList<Node> visitDirectAbsolutePath(ExpressionGrammarParser.DirectAbsolutePathContext ctx) {
        visitDoc(ctx.doc());
        return visit(ctx.rp());
    }

    @Override public ArrayList<Node> visitIndirectAbsolutePath(ExpressionGrammarParser.IndirectAbsolutePathContext ctx) {
        visitDoc(ctx.doc());

        HashSet<Node> result = new HashSet<>();
        LinkedList<Node> bfsQueue = new LinkedList<>();
        bfsQueue.addLast(xmlDocument.getDocumentElement());
        while(!bfsQueue.isEmpty()){
            this.currentNode = bfsQueue.getFirst();
            result.addAll(visit(ctx.rp()));
            for(int i=0; i<this.currentNode.getChildNodes().getLength(); i++){
                bfsQueue.addLast(this.currentNode.getChildNodes().item(i));
            }
            bfsQueue.removeFirst();
        }
        return new ArrayList<>(result);
    }

    @Override public ArrayList<Node> visitDoc(ExpressionGrammarParser.DocContext ctx) {
        try {
            File xmlFile = new File(parseStr(ctx.fileName().getText()));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.xmlDocument = builder.parse(xmlFile);
            this.currentNode = xmlDocument.getDocumentElement().getParentNode();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public ArrayList<Node> visitTagRelativePath(ExpressionGrammarParser.TagRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        String tagName = ctx.getText();

        NodeList elements = currentNode.getChildNodes();
        for(int i=0; i<elements.getLength(); i++){
            if(elements.item(i).getNodeName().equals(tagName)){
                result.add(elements.item(i));
            }
        }

        return result;
    }

    @Override
    public ArrayList<Node> visitCurrentRelativePath(ExpressionGrammarParser.CurrentRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        result.add(currentNode);
        return result;
    }

    @Override
    public ArrayList<Node> visitChildrenRelativePath(ExpressionGrammarParser.ChildrenRelativePathContext ctx) {
        HashSet<Node> result = new HashSet<>();
        LinkedList<Node> bfsQueue = new LinkedList<>();
        bfsQueue.addLast(currentNode);
        while(!bfsQueue.isEmpty()){
            Node node = bfsQueue.getFirst();
            for(int i=0; i<node.getChildNodes().getLength(); i++){
                result.add(node.getChildNodes().item(i));
                if(node.getChildNodes().item(i).hasChildNodes()){
                    bfsQueue.addLast(node.getChildNodes().item(i));
                }
            }
            bfsQueue.removeFirst();
        }
        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Node> visitUpperRelativePath(ExpressionGrammarParser.UpperRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        result.add(currentNode.getParentNode());
        return result;
    }

    @Override
    public ArrayList<Node> visitTextRelativePath(ExpressionGrammarParser.TextRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        NodeList children = currentNode.getChildNodes(); // "The text node associated to element node"? Does it mean the child node for text node?
        for(int i=0; i<children.getLength(); i++){
            if(children.item(i).getNodeType()==Node.TEXT_NODE){
                result.add(children.item(i));
            }
        }
        return result;
    }

    @Override
    public ArrayList<Node> visitAttribRelativePath(ExpressionGrammarParser.AttribRelativePathContext ctx) {
        String attrName = ctx.attName().ID().getText();
        ArrayList<Node> result = new ArrayList<>();
        result.add(currentNode.getAttributes().getNamedItem(attrName));
        return result;
    }

    @Override
    public ArrayList<Node> visitParentheseRelativePath(ExpressionGrammarParser.ParentheseRelativePathContext ctx) {
        return visit(ctx.rp());
    }

    @Override
    public ArrayList<Node> visitDirectRelativePath(ExpressionGrammarParser.DirectRelativePathContext ctx) {
        ArrayList<Node> x = visit(ctx.rp(0));
        HashSet<Node> result = new HashSet<>();
        for(Node node: x){
            this.currentNode = node;
            result.addAll(visit(ctx.rp(1)));
        }

        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Node> visitFilterRelativePath(ExpressionGrammarParser.FilterRelativePathContext ctx) {
        this.currNodes = visit(ctx.rp());
        return visit(ctx.f());
    }

    @Override
    public ArrayList<Node> visitCommaRelativePath(ExpressionGrammarParser.CommaRelativePathContext ctx) {
        ArrayList<Node> res = visit(ctx.rp(0));
        ArrayList<Node> tmp = visit(ctx.rp(1));

        for (Node node : tmp) {
            // contains solved
            if (!res.contains(node)) {
                res.add(node);
            }
        }
        return res;
    }

    @Override
    public ArrayList<Node> visitRpFilter(ExpressionGrammarParser.RpFilterContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        Node nodeRestore = this.currentNode;
        for (Node currNode: currNodes) {
            this.currentNode = currNode;
            if (!visit(ctx.rp()).isEmpty()){
                res.add(currNode);
            }
        }
        this.currentNode = nodeRestore;
//        if (visit(ctx.rp()).size() != 0) {
//            res.add(this.currentNode);
//        }
        return res;
    }

    @Override
    public ArrayList<Node> visitEqual1Filter(ExpressionGrammarParser.Equal1FilterContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        Node nodeRestore = this.currentNode;
        for (Node currNode: currNodes) {
            this.currentNode = currNode;
            ArrayList<Node> rp1 = visit(ctx.rp(0));
            this.currentNode = currNode;
            ArrayList<Node> rp2 = visit(ctx.rp(1));

            boolean breakFlag = false;
            for (Node x: rp1) {
                for (Node y: rp2) {
                    if (x.isEqualNode(y)) {
                        res.add(currNode);
                        breakFlag = true;
                        break;
                    }
                }
                if(breakFlag){
                    break;
                }
            }
        }
        this.currentNode = nodeRestore;
        return res;
    }

    @Override
    public ArrayList<Node> visitEqual2Filter(ExpressionGrammarParser.Equal2FilterContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        Node nodeRestore = this.currentNode;
        for (Node currNode: currNodes) {
            this.currentNode = currNode;
            ArrayList<Node> rp1 = visit(ctx.rp(0));
            this.currentNode = currNode;
            ArrayList<Node> rp2 = visit(ctx.rp(1));

            boolean breakFlag = false;
            for (Node x: rp1) {
                for (Node y: rp2) {
                    if (x.isEqualNode(y)) {
                        result.add(currNode);
                        breakFlag = true;
                        break;
                    }
                }
                if(breakFlag){
                    break;
                }
            }
        }
        this.currentNode = nodeRestore;
        return result;
    }

    @Override
    public ArrayList<Node> visitIsFilter(ExpressionGrammarParser.IsFilterContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        Node nodeRestore = this.currentNode;
        for (Node currNode: currNodes) {
            this.currentNode = currNode;
            ArrayList<Node> rp1 = visit(ctx.rp(0));
            this.currentNode = currNode;
            ArrayList<Node> rp2 = visit(ctx.rp(1));

            boolean breakFlag = false;
            for (Node x: rp1) {
                for (Node y: rp2) {
                    if (x.isSameNode(y)) {
                        res.add(currNode);
                        breakFlag = true;
                        break;
                    }
                }
                if(breakFlag){
                    break;
                }
            }
        }
        this.currentNode = nodeRestore;
        return res;
    }

    @Override
    public ArrayList<Node> visitStringconstantFilter(ExpressionGrammarParser.StringconstantFilterContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        Node nodeRestore = this.currentNode;
        for (Node currNode: currNodes) {
            this.currentNode = currNode;
            ArrayList<Node> rp = visit(ctx.rp());
            String s = parseStr(ctx.getText());
            for (Node x : rp) {
                // TODO: getTextContent.
                if (x.getTextContent().equals(s)){
                    res.add(currNode);
                    break;
                }
            }
        }
        this.currentNode = nodeRestore;
        return res;
    }

    @Override
    public ArrayList<Node> visitParentheseFilter(ExpressionGrammarParser.ParentheseFilterContext ctx) {
        return visit(ctx.f());
    }

    @Override
    public ArrayList<Node> visitAndFilter(ExpressionGrammarParser.AndFilterContext ctx) {
        //ArrayList<Node> res = new ArrayList<>();
        ArrayList<Node> f1 = visit(ctx.f(0));
        ArrayList<Node> f2 = visit(ctx.f(1));
        f1.retainAll(f2);
        return f1;
    }

    @Override
    public ArrayList<Node> visitIndirectRelativePath(ExpressionGrammarParser.IndirectRelativePathContext ctx) {
        ArrayList<Node> x = visit(ctx.rp(0));

        HashSet<Node> result = new HashSet<>();
        LinkedList<Node> bfsQueue = new LinkedList<>();
        for(Node node: x){
            bfsQueue.addLast(node);
            while(!bfsQueue.isEmpty()){
                this.currentNode = bfsQueue.getFirst();
                result.addAll(visit(ctx.rp(1)));
                for(int i=0; i<this.currentNode.getChildNodes().getLength(); i++){
                    bfsQueue.addLast(this.currentNode.getChildNodes().item(i));
                }
                bfsQueue.removeFirst();
            }
        }
        return new ArrayList<>(result);
    }
    @Override
    public ArrayList<Node> visitOrFilter(ExpressionGrammarParser.OrFilterContext ctx) {
        ArrayList<Node> f1 = visit(ctx.f(0));
        ArrayList<Node> f2 = visit(ctx.f(1));
        // remove duplicate when adding f2 to f1
        for (Node f2Node: f2) {
            if (! f1.contains(f2Node)) {
                f1.add(f2Node);
            }
        }
        return f1;
    }

    @Override
    public ArrayList<Node> visitNotFilter(ExpressionGrammarParser.NotFilterContext ctx) {
        ArrayList<Node> f1 = visit(ctx.f());
        ArrayList<Node> res = new ArrayList<>();
        for (Node currNode: currNodes) {
            if (!f1.contains(currNode)) {
                res.add(currNode);
            }
        }
        return res;
    }
}
