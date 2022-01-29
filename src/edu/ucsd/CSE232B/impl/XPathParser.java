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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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

    @Override public ArrayList<Node> visitDirectAbsolutePath(ExpressionGrammarParser.DirectAbsolutePathContext ctx) {
        visitDoc(ctx.doc());
        return visit(ctx.rp());
    }

    @Override public ArrayList<Node> visitIndirectAbsolutePath(ExpressionGrammarParser.IndirectAbsolutePathContext ctx) {
        visitDoc(ctx.doc());

        ArrayList<Node> result = new ArrayList<>();
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
        return result;
    }

    @Override public ArrayList<Node> visitDoc(ExpressionGrammarParser.DocContext ctx) {
        try {
            File xmlFile = new File(ctx.fileName().getText().substring(1, ctx.fileName().getText().length()-1));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.xmlDocument = builder.parse(xmlFile);
            this.currentNode = xmlDocument.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public ArrayList<Node> visitTagRelativePath(ExpressionGrammarParser.TagRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        String tagName = ctx.getText();

        if(currentNode instanceof Element){
            Element currentElement = (Element)this.currentNode;
            if(currentElement.getTagName().equals(tagName)){
                result.add(this.currentNode);
            }
        }

        return result;
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
            if (!res.contains(node)) {
                res.add(node);
            }
        }
        return res;
    }

    @Override
    public ArrayList<Node> visitRpFilter(ExpressionGrammarParser.RpFilterContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        if (visit(ctx.rp()).size() != 0) {
            res.add(this.currentNode);
        }
        return res;
    }

    @Override
    public ArrayList<Node> visitEqualFilter(ExpressionGrammarParser.EqualFilterContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        ArrayList<Node> rp1 = visit(ctx.rp(0));
        ArrayList<Node> rp2 = visit(ctx.rp(1));
        for (Node x: rp1) {
            for (Node y: rp2) {
                if (x.isEqualNode(y)) {
                    res.add(this.currentNode);
                }
            }
        }
        return res;
    }

    @Override
    public ArrayList<Node> visitIsFilter(ExpressionGrammarParser.IsFilterContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        ArrayList<Node> rp1 = visit(ctx.rp(0));
        ArrayList<Node> rp2 = visit(ctx.rp(1));
        for (Node x: rp1) {
            for (Node y: rp2) {
                if (x.isSameNode(y)) {
                    res.add(this.currentNode);
                }
            }
        }
        return res;
    }

    @Override
    public ArrayList<Node> visitStringconstantFilter(ExpressionGrammarParser.StringconstantFilterContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        ArrayList<Node> rp = visit(ctx.rp());
        String s = ctx.getText().substring(1, ctx.getText().length()-1);
        for (Node x : rp) {
            if (x.getTextContent().equals(s)){
                res.add(this.currentNode);
            }
        }
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
    public ArrayList<Node> visitOrFilter(ExpressionGrammarParser.OrFilterContext ctx) {
        ArrayList<Node> f1 = visit(ctx.f(0));
        ArrayList<Node> f2 = visit(ctx.f(1));
        f1.addAll(f2);
        return f1;
    }

    @Override
    public ArrayList<Node> visitNotFilter(ExpressionGrammarParser.NotFilterContext ctx) {
        return super.visitNotFilter(ctx);
    }
}
