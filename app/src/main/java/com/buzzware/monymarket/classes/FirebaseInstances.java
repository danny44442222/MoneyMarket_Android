package com.buzzware.monymarket.classes;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseInstances {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static CollectionReference usersCollection = firebaseFirestore.collection("users");
    public static CollectionReference chatCollection = firebaseFirestore.collection("Chats");
    public static CollectionReference groupsCollection = firebaseFirestore.collection("ClientChatGroups");

    public static CollectionReference adminChatCollection = firebaseFirestore.collection("AdminChats");
}
