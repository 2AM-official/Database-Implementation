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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.*;

public class XPathParser extends ExpressionGrammarBaseVisitor<ArrayList<Node>> {
    private Document xmlDocument;
    private Node currentNode;
    ArrayList<Node> currNodes = new ArrayList<>();
    HashMap<String, ArrayList<Node>> variableMap = new HashMap<>();

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
        Node nodeRestore = this.currentNode;
        while(!bfsQueue.isEmpty()){
            this.currentNode = bfsQueue.getFirst();
            result.addAll(visit(ctx.rp()));
            for(int i=0; i<bfsQueue.getFirst().getChildNodes().getLength(); i++){
                if(bfsQueue.getFirst().getChildNodes().item(i) instanceof Element){
                    bfsQueue.addLast(bfsQueue.getFirst().getChildNodes().item(i));
                }
            }
            bfsQueue.removeFirst();
        }
        this.currentNode = nodeRestore;
        return new ArrayList<>(result);
    }

    @Override public ArrayList<Node> visitDoc(ExpressionGrammarParser.DocContext ctx) {
        try {
            File xmlFile = new File(parseStr(ctx.fileName().getText()));
            if(xmlDocument == null || !xmlFile.toURI().toString().equals(xmlDocument.getDocumentURI())){
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                this.xmlDocument = builder.parse(xmlFile);
            }
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
        ArrayList<Node> result = new ArrayList<>();
        NodeList elements = currentNode.getChildNodes();
        for(int i=0; i<elements.getLength(); i++){
            if(elements.item(i) instanceof Element){
                result.add(elements.item(i));
            }
        }

        return result;
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
        Node originalCurNode = currentNode;
        for(Node node: x){
            this.currentNode = node;
            result.addAll(visit(ctx.rp(1)));
        }
        currentNode = originalCurNode;

        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Node> visitFilterRelativePath(ExpressionGrammarParser.FilterRelativePathContext ctx) {
        this.currNodes = visit(ctx.rp());
        return visit(ctx.f());
    }

    @Override
    public ArrayList<Node> visitCommaRelativePath(ExpressionGrammarParser.CommaRelativePathContext ctx) {
        Node oriCurNode = currentNode;
        ArrayList<Node> res = visit(ctx.rp(0));
        currentNode = oriCurNode;
        ArrayList<Node> tmp = visit(ctx.rp(1));
        currentNode = oriCurNode;

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
            String s = parseStr(ctx.STRCON().getText());
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
        Node nodeRestore = this.currentNode;
        for(Node node: x){
            bfsQueue.addLast(node);
            while(!bfsQueue.isEmpty()){
                this.currentNode = bfsQueue.getFirst();
                result.addAll(visit(ctx.rp(1)));
                for(int i=0; i<bfsQueue.getFirst().getChildNodes().getLength(); i++){
                    bfsQueue.addLast(bfsQueue.getFirst().getChildNodes().item(i));
                }
                bfsQueue.removeFirst();
            }
        }
        this.currentNode = nodeRestore;
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

    @Override
    public ArrayList<Node> visitVarXQ(ExpressionGrammarParser.VarXQContext ctx) {
        String varName = ctx.var().ID().getText();
        if(variableMap.containsKey(varName)){
            return new ArrayList<>(variableMap.get(varName));
        }
        return null;
    }

    @Override
    public ArrayList<Node> visitStrXQ(ExpressionGrammarParser.StrXQContext ctx) {
        Node textNode = xmlDocument.createTextNode(parseStr(ctx.STRCON().getText()));
        ArrayList<Node> result = new ArrayList<>();
        result.add(textNode);
        return result;
    }

    @Override
    public ArrayList<Node> visitApXQ(ExpressionGrammarParser.ApXQContext ctx) {
        return visit(ctx.ap());
    }

    @Override
    public ArrayList<Node> visitBraceXQ(ExpressionGrammarParser.BraceXQContext ctx) {
        return visit(ctx.xq());
    }

    @Override
    public ArrayList<Node> visitCommaXQ(ExpressionGrammarParser.CommaXQContext ctx) {
        ArrayList<Node> result = visit(ctx.xq(0));
        result.addAll(visit(ctx.xq(1)));

        return result;
    }

    @Override
    public ArrayList<Node> visitDirectXQ(ExpressionGrammarParser.DirectXQContext ctx) {
        ArrayList<Node> x = visit(ctx.xq());
        HashSet<Node> result = new HashSet<>();
        Node originalCurNode = currentNode;
        for(Node node: x){
            this.currentNode = node;
            result.addAll(visit(ctx.rp()));
        }
        currentNode = originalCurNode;

        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Node> visitIndirectXQ(ExpressionGrammarParser.IndirectXQContext ctx) {
        ArrayList<Node> x = visit(ctx.xq());

        HashSet<Node> result = new HashSet<>();
        LinkedList<Node> bfsQueue = new LinkedList<>();
        Node nodeRestore = this.currentNode;
        for(Node node: x){
            bfsQueue.addLast(node);
            while(!bfsQueue.isEmpty()){
                this.currentNode = bfsQueue.getFirst();
                result.addAll(visit(ctx.rp()));
                for(int i=0; i<bfsQueue.getFirst().getChildNodes().getLength(); i++){
                    bfsQueue.addLast(bfsQueue.getFirst().getChildNodes().item(i));
                }
                bfsQueue.removeFirst();
            }
        }
        this.currentNode = nodeRestore;
        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Node> visitTagNameXQ(ExpressionGrammarParser.TagNameXQContext ctx){
        if(!ctx.tagName(0).getText().equals(ctx.tagName(1).getText())){
            try {
                throw new Exception("tag name not same");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        ArrayList<Node> children = new ArrayList<>();
        for(int i=0; i<ctx.xq().size(); i++){
            children.addAll(visit(ctx.xq(i)));
        }
        Element ele = xmlDocument.createElement(ctx.tagName(0).ID().getText());
        for(Node child: children){
            Node childCopy = child.cloneNode(true);
            ele.appendChild(childCopy);
        }
        ArrayList<Node> result = new ArrayList<>();
        result.add(ele);
        return result;
    }


    @Override
    public ArrayList<Node> visitEq1Cond(ExpressionGrammarParser.Eq1CondContext ctx) {
        ArrayList<Node> pointer = new ArrayList<>();
        ArrayList<Node> c1 = visit(ctx.xq(0));
        ArrayList<Node> c2 = visit(ctx.xq(1));
        for (Node n1: c1) {
            for (Node n2: c2) {
                if (n1.isEqualNode(n2)) {
                    pointer.add(n1);
                    return pointer;
                }
            }
        }
        return pointer;
    }

    @Override
    public ArrayList<Node> visitEq2Cond(ExpressionGrammarParser.Eq2CondContext ctx) {
        ArrayList<Node> pointer = new ArrayList<>();
        ArrayList<Node> c1 = visit(ctx.xq(0));
        ArrayList<Node> c2 = visit(ctx.xq(1));
        for (Node n1: c1) {
            for (Node n2: c2) {
                if(n1.getTextContent().equals("FLAVIUS")){
                    n1=n1;
                }
                if (n1.isEqualNode(n2)) {
                    pointer.add(n1);
                    return pointer;
                }
            }
        }
        return pointer;
    }

    @Override
    public ArrayList<Node> visitIsCond(ExpressionGrammarParser.IsCondContext ctx) {
        ArrayList<Node> pointer = new ArrayList<>();
        ArrayList<Node> c1 = visit(ctx.xq(0));
        ArrayList<Node> c2 = visit(ctx.xq(1));
        for (Node n1: c1) {
            for (Node n2 : c2) {
                if (n1.isSameNode(n2)) {
                    pointer.add(n1);
                    return pointer;
                }
            }
        }
        return pointer;
    }

    @Override
    public ArrayList<Node> visitEmptyCond(ExpressionGrammarParser.EmptyCondContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        //Node node= this.currentNode;
        ArrayList<Node> xq1 = visit(ctx.xq());
        if (xq1.isEmpty()) {
            Node flag = xmlDocument.createTextNode("pointer");
            res.add(flag);
        }
        return res;
    }

    private boolean backtracking(ExpressionGrammarParser.ParSatisfyCondContext ctx, int depth, int length){
        if(depth==length){
            return !visit(ctx.cond()).isEmpty();
        }
        String varName = ctx.var(depth).ID().getText();
        ArrayList<Node> nodes = visit(ctx.xq(depth));
        ArrayList<Node> origin = variableMap.get(varName);
        for(Node node: nodes){
            ArrayList<Node> newVal = new ArrayList<>();
            newVal.add(node);
            variableMap.put(varName, newVal);
            if(backtracking(ctx, depth+1, length)){
                variableMap.put(varName, origin);
                return true;
            }
        }
        variableMap.put(varName, origin);
        return false;
    }

    @Override
    public ArrayList<Node> visitParSatisfyCond(ExpressionGrammarParser.ParSatisfyCondContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        if(backtracking(ctx, 0, ctx.var().size())){
            result.add(xmlDocument.createElement("pointer"));
        }
        return result;
//        ArrayList<Node> res = new ArrayList<>();
//
//        Node nodeStore = this.currentNode;
//        HashMap<String, ArrayList<Node>> ctxStore = this.variableMap;
//
//        int n = ctx.xq().size();
//        ArrayList<ArrayList<Node>> xqResultArr = new ArrayList<>();
//        ArrayList<Integer> selectArr = new ArrayList<>();
//
//        for(int i=0; i<n; i++){
//            xqResultArr.add(visit(ctx.xq(i)));
//            selectArr.add(0);
//            if(xqResultArr.get(i).isEmpty()){
//                return new ArrayList<>();
//            }
//            ArrayList<Node> newVariableVal = new ArrayList<>();
//            newVariableVal.add(xqResultArr.get(i).get(0));
//            this.variableMap.put(ctx.var(i).ID().getText(), newVariableVal);
//        }
//
//        while(selectArr.get(0)<xqResultArr.get(0).size()){
//            boolean flag = false;
//            if(visit(ctx.cond()).size()>0){
//                Node ele = xmlDocument.createTextNode("pointer");
//                res.add(ele);
//                return res;
//            }
//
//            // update selected index list
//            for(int i=n-1; i>=0; i--){
//                if(selectArr.get(i)+1>=xqResultArr.get(i).size()){
//                    // if the selected node is the last node of the result array
//                    if(i==0){
//                        flag = true;
//                        break;
//                    }
//                    selectArr.set(i, 0);
//                    ArrayList<Node> newVariableVal = new ArrayList<>();
//                    newVariableVal.add(xqResultArr.get(i).get(0));
//                    this.variableMap.put(ctx.var(i).ID().getText(), newVariableVal);
//                    continue;
//                }
//                selectArr.set(i, selectArr.get(i)+1);
//                ArrayList<Node> newVariableVal = new ArrayList<>();
//                newVariableVal.add(xqResultArr.get(i).get(selectArr.get(i)));
//                this.variableMap.put(ctx.var(i).ID().getText(), newVariableVal);
//                break;
//            }
//
//            if(flag){
//                break;
//            }
//        }
//
//        this.currentNode = nodeStore;
//        this.variableMap = ctxStore;
//
//        return res;

    }

    @Override
    public ArrayList<Node> visitBraceCond(ExpressionGrammarParser.BraceCondContext ctx) {
        return visit(ctx.cond());
    }

    @Override
    public ArrayList<Node> visitAndCond(ExpressionGrammarParser.AndCondContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        ArrayList<Node> c1 = visit(ctx.cond(0));
        ArrayList<Node> c2 = visit(ctx.cond(1));

        if (!c1.isEmpty() && !c2.isEmpty()) {
            Node flag = xmlDocument.createTextNode("pointer");
            res.add(flag);
        }
        return res;
    }

    @Override
    public ArrayList<Node> visitOrCond(ExpressionGrammarParser.OrCondContext ctx) {
        ArrayList<Node> c1 = visit(ctx.cond(0));
        ArrayList<Node> c2 = visit(ctx.cond(1));

        if (!c1.isEmpty()) return c1;
        if (!c2.isEmpty()) return c2;
        return c1;
    }

    @Override
    public ArrayList<Node> visitNotCond(ExpressionGrammarParser.NotCondContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        ArrayList<Node> c1 = visit(ctx.cond());

        if (c1.isEmpty()) {
            Node flag = xmlDocument.createTextNode("pointer");
            res.add(flag);
        }
        return res;
    }

//    @Override
//    public ArrayList<Node> visitLetXQ(ExpressionGrammarParser.LetXQContext ctx) {
//        int size = ctx.letClause().var().size();
//        for (int i = 0; i < size; i++) {
//            variableMap.put(ctx.letClause().var(i).ID().getText(), visit(ctx.letClause().xq(i)));
//        }
//        return visit(ctx.letClause().xq(size));
//    }

    @Override
    public ArrayList<Node> visitStatementXQ(ExpressionGrammarParser.StatementXQContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        HashMap<String, ArrayList<Node>> current = new HashMap<>(variableMap);
        ArrayList<HashMap<String, ArrayList<Node>>> path = new ArrayList<>();

        path.add(current);
        helper(ctx, 0, ctx.forClause().var().size(), result, path);
        //TODO: potential 0 or the last one
        variableMap = path.get(0);
        return result;
    }

    //TODO: might have bugs
    private void helper(ExpressionGrammarParser.StatementXQContext ctx, int depth, int length, ArrayList<Node> result,
                        ArrayList<HashMap<String, ArrayList<Node>>> path) {
        if (depth == length) {
            if (ctx.letClause() != null) visit(ctx.letClause());
            if (ctx.whereClause() != null && visit(ctx.whereClause()).size() == 0) {
                return;
            }
            result.addAll(visit(ctx.returnClause()));
            return;
        }

        String k = ctx.forClause().var(depth).ID().getText();
        ArrayList<Node> nodes = visit(ctx.forClause().xq(depth));
        for (Node node: nodes) {
            HashMap<String, ArrayList<Node>> n = new HashMap<>(variableMap);
            ArrayList<Node> v= new ArrayList<>();
            v.add(node);
            n.put(k, v);
            if(k.equals("x")){
                System.out.println("for "+k+"="+node.getTextContent());
            }

            path.add(n);
            variableMap = path.get(path.size()-1);
            helper(ctx, depth+1, length, result, path);
            path.remove(path.size()-1);
            variableMap = path.get(path.size()-1);
        }
    }


    private boolean checkJoin(Node tuple1, Node tuple2, ArrayList<String> tagNames1, ArrayList<String> tagNames2){
        HashMap<String, Node> tupleMap1 = new HashMap<>();
        HashMap<String, Node> tupleMap2 = new HashMap<>();

        for(int i=0; i<tuple1.getChildNodes().getLength(); i++){
            Node node = tuple1.getChildNodes().item(i);
            tupleMap1.put(node.getNodeName(), node);
        }

        for(int j=0; j<tuple2.getChildNodes().getLength(); j++){
            Node node = tuple2.getChildNodes().item(j);
            tupleMap2.put(node.getNodeName(), node);
        }

        for(int i=0; i<tagNames1.size(); i++){
            if(!tupleMap1.get(tagNames1.get(i)).isEqualNode(tupleMap2.get(tagNames2.get(i)))){
                return false;
            }
        }

        return true;
    }

    private ArrayList<Node> join(ArrayList<Node> xqRes1, ArrayList<Node> xqRes2, ArrayList<String> tagName1, ArrayList<String> tagName2){
        ArrayList<Node> result = new ArrayList<>();
        HashMap<String, ArrayList<Node>> joinHashMap = new HashMap<>();
        // generate hash map
        for(Node tuple: xqRes1){
            StringBuilder keyBuilder = new StringBuilder();
            HashMap<String, String> tupleMap = new HashMap<>();
            for(int i=0; i<tuple.getChildNodes().getLength(); i++){
                tupleMap.put(tuple.getChildNodes().item(i).getNodeName(), tuple.getChildNodes().item(i).getTextContent());
//                System.out.println(tuple.getChildNodes().item(i).getTextContent());
            }
            // generate key string
            for(String tagName: tagName1){
                keyBuilder.append(tupleMap.get(tagName));
                keyBuilder.append(' ');
            }
            if(!joinHashMap.containsKey(keyBuilder.toString())){
                ArrayList<Node> val = new ArrayList<>();
                val.add(tuple);
                joinHashMap.put(keyBuilder.toString(), val);
            }else{
                joinHashMap.get(keyBuilder.toString()).add(tuple);
            }
//            System.out.println(tuple.getChildNodes().getLength());
        }

        for(Node tuple: xqRes2){
            StringBuilder keyBuilder = new StringBuilder();
            HashMap<String, String> tupleMap = new HashMap<>();
            for(int i=0; i<tuple.getChildNodes().getLength(); i++){
                tupleMap.put(tuple.getChildNodes().item(i).getNodeName(), tuple.getChildNodes().item(i).getTextContent());
//                System.out.println(tuple.getChildNodes().item(i).getTextContent());
            }
            // generate key string
            for(String tagName: tagName2){
                keyBuilder.append(tupleMap.get(tagName));
                keyBuilder.append(' ');
            }

            if(joinHashMap.containsKey(keyBuilder.toString())){
                for(Node tuple2: joinHashMap.get(keyBuilder.toString())){
//                    System.out.println(tuple2.getChildNodes().getLength());
                    Node newTuple = tuple.cloneNode(true);
                    for(int i=0; i<tuple2.getChildNodes().getLength(); i++){
                        newTuple.appendChild(tuple2.getChildNodes().item(i).cloneNode(true));
                    }
//                    for(int i=0; i<tuple2.getChildNodes().getLength(); i++){
//                        System.out.println(tuple2.getChildNodes().item(i).getNodeName());
//                    }
//                    System.out.println();
                    result.add(newTuple);
                }
            }
        }

        return result;
    }

    @Override
    public ArrayList<Node> visitJoinXQ(ExpressionGrammarParser.JoinXQContext ctx) {
//        System.out.println(ctx.xq(0).getText());
        ArrayList<Node> xqRes1 = visit(ctx.xq(0));
        ArrayList<Node> xqRes2 = visit(ctx.xq(1));

        ArrayList<String> tagName1 = new ArrayList<>();
        ArrayList<String> tagName2 = new ArrayList<>();

        for(int i=0; i<ctx.nameListClause(0).ID().size(); i++){
            tagName1.add(ctx.nameListClause(0).ID(i).getText());
        }

        for(int i=0; i<ctx.nameListClause(1).ID().size(); i++){
            tagName2.add(ctx.nameListClause(1).ID(i).getText());
        }

//        FileWriter fw = null;
//        try {
//            fw = new FileWriter("output.file");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for(Node node: xqRes2){
//            try {
//                fw.write(node.getChildNodes().getLength()+"\n");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        // Set up the output transformer
//        TransformerFactory transfac = TransformerFactory.newInstance();
//        Transformer trans = null;
//        try {
//            trans = transfac.newTransformer();
//        } catch (TransformerConfigurationException e) {
//            e.printStackTrace();
//        }
//        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//        trans.setOutputProperty(OutputKeys.INDENT, "yes");
//        trans.setOutputProperty(OutputKeys.METHOD, "xml");
//        // Print the DOM node
//        FileWriter sw = null;
//        try {
//            sw = new FileWriter("output_file");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        StreamResult res = new StreamResult(sw);
//
//        for (int i = 0; i < xqRes1.size(); i++) {
//            DOMSource source = new DOMSource(xqRes1.get(i));
//            try {
//                trans.transform(source, res);
//            } catch (TransformerException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            sw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return join(xqRes1, xqRes2, tagName1, tagName2);

//        ArrayList<Node> result = new ArrayList<>();
//        for(Node tuple1 : xqRes1){
//            for(Node tuple2: xqRes2){
//                if(checkJoin(tuple1, tuple2, tagName1, tagName2)){
//                    for(int i=0; i<tuple2.getChildNodes().getLength(); i++){
//                        tuple1.appendChild(tuple2.getChildNodes().item(i));
//                    }
//                    result.add(tuple1);
//                }
//            }
//        }
//        ArrayList<Node> xqResSmall = null;
//        ArrayList<Node> xqResBig = null;
//        if(xqRes1.size()>xqRes2.size()){
//            xqResSmall = xqRes2;
//            xqResBig = xqRes1;
//        }

//        HashMap<String, ArrayList<Node>> joinHashMap = new HashMap<>();
//        // generate hash map
//        for(Node tuple: xqRes1){
//            StringBuilder keyBuilder = new StringBuilder();
//            HashMap<String, String> tupleMap = new HashMap<>();
//            for(int i=0; i<tuple.getChildNodes().getLength(); i++){
//                tupleMap.put(tuple.getChildNodes().item(i).getNodeName(), tuple.getChildNodes().item(i).toString());
//                System.out.println(tuple.getChildNodes().item(i).toString());
//            }
//            // generate key string
//            for(int i=0; i<tuple.getChildNodes().getLength(); i++){
//                for(String tagName: tagName1){
//                    keyBuilder.append(tupleMap.get(tagName));
//                    keyBuilder.append(' ');
//                }
//            }
//            if(!joinHashMap.containsKey(keyBuilder.toString())){
//                ArrayList<Node> val = new ArrayList<>();
//                val.add(tuple);
//                joinHashMap.put(keyBuilder.toString(), val);
//            }else{
//                joinHashMap.get(keyBuilder.toString()).add(tuple);
//            }
//        }
//
//        for(Node tuple: xqRes2){
//            StringBuilder keyBuilder = new StringBuilder();
//            HashMap<String, String> tupleMap = new HashMap<>();
//            for(int i=0; i<tuple.getChildNodes().getLength(); i++){
//                tupleMap.put(tuple.getChildNodes().item(i).getNodeName(), tuple.getChildNodes().item(i).toString());
//                System.out.println(tuple.getChildNodes().item(i).toString());
//            }
//            // generate key string
//            for(int i=0; i<tuple.getChildNodes().getLength(); i++){
//                for(String tagName: tagName2){
//                    keyBuilder.append(tupleMap.get(tagName));
//                    keyBuilder.append(' ');
//                }
//            }
//
//            if(joinHashMap.containsKey(keyBuilder.toString())){
//                for(Node tuple2: joinHashMap.get(keyBuilder.toString())){
//                    Node newTuple = tuple.cloneNode(true);
//                    for(int i=0; i<tuple2.getChildNodes().getLength(); i++){
//                        newTuple.appendChild(tuple2.getChildNodes().item(i));
//                    }
//                    result.add(newTuple);
//                }
//            }
//        }
//
//        return result;
    }

    @Override
    public ArrayList<Node> visitLetClause(ExpressionGrammarParser.LetClauseContext ctx) {
        ArrayList<Node> res = new ArrayList<>();
        int size = ctx.var().size();
        for (int i = 0; i < size; i++) {
            this.variableMap.put(ctx.var(i).ID().getText(), visit(ctx.xq(i)));
        }
        return res;
    }

    @Override
    public ArrayList<Node> visitWhereClause(ExpressionGrammarParser.WhereClauseContext ctx) {
        return visit(ctx.cond());
    }

    @Override
    public ArrayList<Node> visitReturnClause(ExpressionGrammarParser.ReturnClauseContext ctx) {
        return visit(ctx.xq());
    }
}
