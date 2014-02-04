package com.engagepoint.university.admincentre;

import com.engagepoint.university.admincentre.dao.NodeDAO;

import com.engagepoint.university.admincentre.entity.Node;


public class DBInfinispan {


    public static void setNodeDAO(){
        NodeDAO nodeDAO = new NodeDAO();
        Node nodeParent = nodeDAO.getRoot();

        Node nodeChild1 = new Node();
        Node nodeChild2 = new Node();


        nodeChild1.setName("Child1");
        nodeChild2.setName("Child2");


        nodeParent.addChildNodeId(nodeChild1);
        nodeParent.addChildNodeId(nodeChild2);

       /* nodeChild1.setParentNodeId(nodeParent.getId());
        nodeChild2.setParentNodeId(nodeParent.getId());*/

        try {
            nodeDAO.create(nodeChild2);
            nodeDAO.create(nodeChild1);
            nodeDAO.update(nodeParent);
                   } catch (Exception e) {
            e.printStackTrace();
        }

        nodeDAO.toString();

//
//        Node nodeChild11 = new Node();
//        Node nodeChild22 = new Node();
//        Node nodeChild33 = new Node();
//
//        nodeChild11.setName("Child11");
//        nodeChild22.setName("Child22");
//        nodeChild33.setName("Child33");

//        nodeChild11.setParentNodeId(nodeChild1.getId());
//        nodeChild22.setParentNodeId(nodeChild1.getId());
//        nodeChild33.setParentNodeId(nodeChild1.getId());

//        nodeParent.addChildNodeId(nodeChild11);
//        nodeParent.addChildNodeId(nodeChild22);
//        nodeParent.addChildNodeId(nodeChild33);

//
//        List<Node> list1 = new ArrayList<Node>();
//        list1.add(nodeChild11);
//        list1.add(nodeChild22);
//        list1.add(nodeChild33);
//
//        List<com.engagepoint.university.admincentre.Key> keyList1 = new ArrayList<com.engagepoint.university.admincentre.Key>();
//        keyList1.add(new com.engagepoint.university.admincentre.Key("1", "11", "111"));
//        keyList1.add(new com.engagepoint.university.admincentre.Key("2", "22", "222"));
//        keyList1.add(new com.engagepoint.university.admincentre.Key("3", "33", "333"));
//        keyList1.add(new com.engagepoint.university.admincentre.Key("4", "44", "444"));
//
//        nodeParent.setChildNodes(list);
//        nodeChild1.setChildNodes(list1);
//
//        nodeChild2.setKeys(keyList1);
//        nodeChild33.setKeys(keyList1);


    }


}
