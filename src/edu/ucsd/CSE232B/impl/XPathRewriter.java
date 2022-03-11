package edu.ucsd.CSE232B.impl;

import edu.ucsd.CSE232B.parsers.ExpressionGrammarBaseVisitor;
import edu.ucsd.CSE232B.parsers.ExpressionGrammarParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class XPathRewriter extends ExpressionGrammarBaseVisitor<String> {
    private HashMap<String, String> parents = new HashMap<>();
    HashMap<String, String> varQueryGraph = new HashMap<>();
    HashMap<String, ArrayList<String>> eqMap = new HashMap<>();
    HashMap<String, ArrayList<String>> rootGroupMap = new HashMap<>();

    private String find(String p){
        while(!p.equals(parents.get(p))){
            p = parents.get(p);
        }
        return p;
    }

    private void union(String p, String q){
        if(!find(p).equals(find(q))){
            parents.put(p, q);
        }
    }

    private StringBuilder groupForToString(ArrayList<String> varGroup){
        StringBuilder result = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();
        StringBuilder returnClause = new StringBuilder();
        result.append("for ");
        returnClause.append(" return <tuple>\n");
        for(String var: varGroup){
            result.append(var);
            result.append(" in ");
            result.append(varQueryGraph.get(var));
            result.append(", \n");

            if(eqMap.containsKey(var)){
                for(String eqObj: eqMap.get(var)){
                    if(eqObj.charAt(0)!='$' || find(eqObj).equals(find(var))){
                        if(whereClause.length()>0){
                            whereClause.append("and ");
                        }
                        whereClause.append(var);
                        whereClause.append(" eq ");
                        whereClause.append(eqObj);
                    }
                }
            }

            String varName = var.substring(1);
            returnClause.append(String.format("<%s>{%s}<%s>, \n", varName, var, varName));
        }
        if(whereClause.length()>0){
            whereClause.insert(0, "where ");
        }
        returnClause.append("</tuple>");
        result.append(whereClause);
        result.append(returnClause);
        return result;
    }

    private StringBuilder joinStringGenerate(StringBuilder lastJoin, String groupRootJoining, HashSet<String> groupJoined){
        StringBuilder result = new StringBuilder();
        result.append("join (");
        result.append(lastJoin);
        result.append(", ");
        result.append(groupForToString(rootGroupMap.get(groupRootJoining)));
        result.append(", ");

        StringBuilder joinClauseA = new StringBuilder();
        StringBuilder joinClauseB = new StringBuilder();
        joinClauseA.append("[");
        joinClauseB.append("[");
        for(String var: rootGroupMap.get(groupRootJoining)){
            if(eqMap.containsKey(var)){
                for(String eqObj: eqMap.get(var)){
                    if(eqObj.charAt(0)=='$' && groupJoined.contains(find(eqObj))){
                        if(joinClauseA.length()>1){
                            joinClauseA.append(", ");
                        }
                        if(joinClauseB.length()>1){
                            joinClauseB.append(", ");
                        }
                        joinClauseA.append(var.substring(1));
                        joinClauseB.append(eqObj.substring(1));
                    }
                }
            }
        }
        joinClauseA.append("]");
        joinClauseB.append("]");

        result.append(joinClauseA);
        result.append(", ");
        result.append(joinClauseB);
        result.append(")");

        return result;
    }

    @Override
    public String visitStatementXQ(ExpressionGrammarParser.StatementXQContext ctx) {
        ExpressionGrammarParser.ForClauseContext forClauseContext = ctx.forClause();
        ExpressionGrammarParser.WhereClauseContext whereClauseContext = ctx.whereClause();

        HashMap<String, String> dependencyGraph = new HashMap<>();
        for(int i=0; i<forClauseContext.var().size(); i++){
            String var = ctx.forClause().var(i).getText();
            String xq = ctx.forClause().xq(i).getText();
            varQueryGraph.put(var, xq);

            if(xq.charAt(0)=='$'){
                String dependency = xq.split("/")[0];
                dependencyGraph.put(var, dependency);
            }else{
                dependencyGraph.put(var, null);
            }
        }

        String cond = whereClauseContext.cond().getText();
        String[] condList = cond.split("and");
        for(String eq: condList){
            String[] pair = eq.split("eq");
            pair[0] = pair[0].trim();
            pair[1] = pair[1].trim();
            if(eqMap.containsKey(pair[0])){
                eqMap.get(pair[0]).add(pair[1]);
            }else{
                ArrayList<String> val = new ArrayList<>();
                val.add(pair[1]);
                eqMap.put(pair[0], val);
            }

            if(eqMap.containsKey(pair[1])){
                eqMap.get(pair[1]).add(pair[0]);
            }else{
                ArrayList<String> val = new ArrayList<>();
                val.add(pair[0]);
                eqMap.put(pair[1], val);
            }
        }

        //try to find connected components
        for(String key: dependencyGraph.keySet()){
            parents.put(key, key);
        }

        System.out.println(dependencyGraph);

        for(String key: dependencyGraph.keySet()){
            if(dependencyGraph.get(key)==null){
                continue;
            }
            String pKey = find(key);
            String pDepend = find(dependencyGraph.get(key));
            union(pKey, pDepend);
        }

        System.out.println(parents);

        HashSet<String> groupJoined = new HashSet<>();
        for(String var: parents.keySet()){
            String par = find(var);
            if(rootGroupMap.containsKey(par)){
                rootGroupMap.get(par).add(var);
            }else{
                ArrayList<String> group = new ArrayList<>();
                group.add(var);
                rootGroupMap.put(par, group);
            }
        }

        System.out.println(rootGroupMap);

        StringBuilder resultBuilder = new StringBuilder();
        boolean isFirst = true;
        for(String root: rootGroupMap.keySet()){
            if(isFirst){
                resultBuilder.append(groupForToString(rootGroupMap.get(root)));
                groupJoined.add(root);
                isFirst = false;
                continue;
            }

            resultBuilder = joinStringGenerate(resultBuilder, root, groupJoined);
            groupJoined.add(root);
        }

        

        return resultBuilder.toString();
    }
}
