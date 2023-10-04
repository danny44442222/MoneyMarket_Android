package com.buzzware.monymarket.activities.messages.chat.vm;


import static com.buzzware.monymarket.activities.BaseActivity.getUserId;
import static com.buzzware.monymarket.classes.FirebaseInstances.firebaseFirestore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.buzzware.monymarket.Models.NotificationModel;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.activities.messages.chat.mo.MessageModel;
import com.buzzware.monymarket.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.monymarket.activities.messages.chat.mo.SendConversationModel;
import com.buzzware.monymarket.activities.messages.chat.mo.SendLastMessageModel;
import com.buzzware.monymarket.activities.messages.chatList.mo.ConversationModel;
import com.buzzware.monymarket.activities.messages.chatList.mo.LastMessageModel;
import com.buzzware.monymarket.classes.FirebaseInstances;
import com.buzzware.monymarket.generic.GenericModelLiveData;
import com.buzzware.monymarket.retrofit.NotificationController;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<GenericModelLiveData> data, alreadyExistsOrNotData;
    private MutableLiveData<GenericModelLiveData> messagesData, rideListenerData;

    public LiveData<GenericModelLiveData> getParcelableChatModel() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
    }
    
    public LiveData<GenericModelLiveData> getRideListenerData() {

        if (rideListenerData == null) {

            rideListenerData = new MutableLiveData<>();

        }

        return rideListenerData;
    }
    public LiveData<GenericModelLiveData> getAlreadyExistsCheckData() {

        if (alreadyExistsOrNotData == null) {

            alreadyExistsOrNotData = new MutableLiveData<>();

        }

        return alreadyExistsOrNotData;
    }

    public LiveData<GenericModelLiveData> getMessagesDataModel() {

        if (messagesData == null) {

            messagesData = new MutableLiveData<>();

        }

        return messagesData;
    }

    public void getConversation(String conversationID, CollectionReference reference, ParcelableChat parcelableChat) {

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        reference.document(conversationID)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.getResult() != null) {

                        LastMessageModel lastMessageModel = task.getResult().get("lastMessage", LastMessageModel.class);

                        if (lastMessageModel != null) {

                            parcelableChat.setCurrentUserId(getUserId());
                            parcelableChat.setSelectedUserId(lastMessageModel.fromID);

                            if (parcelableChat.getCurrentUserId().equalsIgnoreCase(lastMessageModel.fromID)) {

                                parcelableChat.setSelectedUserId(lastMessageModel.toID);

                            }

                            parcelableChat.setConversationID(conversationID);

                            getMyImage(parcelableChat);

                        }

                    } else {

                        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "No Conversation Found"));

                    }
                });

    }

    public void getMyImage(ParcelableChat parcelableChat) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("users").document(getUserId());

        reference
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        UserModel user = task.getResult().toObject(UserModel.class);

                        if (user != null) {

                            parcelableChat.setMyImageUrl(user.imageUrl);

                        }

                    }

                    getOtherUserImage(parcelableChat);

                });
    }

    public void getOtherUserImage(ParcelableChat parcelableChat) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("users").document(parcelableChat.getSelectedUserId());

        reference
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        UserModel user = task.getResult().toObject(UserModel.class);

                        if (user != null) {

                            parcelableChat.setOtherUserImageUrl(user.imageUrl);

                        }

                    }

                    data.postValue(new GenericModelLiveData(parcelableChat, GenericModelLiveData.Status.success, null));

                });

    }


    private void filterList(ArrayList<LastMessageModel> list) {


        FirebaseInstances.usersCollection
                .get()
                .addOnCompleteListener(task -> {

                    List<ConversationModel> conversations = new ArrayList<>();

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                            UserModel user = documentSnapshot.toObject(UserModel.class);

                            if (user != null) {

                                user.userId = documentSnapshot.getId();

                                for (int i = 0; i < list.size(); i++) {

                                    String otherUserId = list.get(i).fromID;

                                    if (!getUserId().equalsIgnoreCase(list.get(i).fromID))

                                        otherUserId = list.get(i).toID;


                                    if (user.userId.equalsIgnoreCase(otherUserId)) {

                                        ConversationModel conversation = getConversationModel(list.get(i), user);

                                        conversations.add(conversation);
                                    }

                                }
                            }

                        }

                    }

                    data.postValue(new GenericModelLiveData(conversations, GenericModelLiveData.Status.success, null));

                });

    }


    private ConversationModel getConversationModel(LastMessageModel lastMessageModel, UserModel user) {

        ConversationModel model = new ConversationModel();

        model.conversationID = lastMessageModel.conversationId;
        model.name = user.fullname;
        model.image = user.imageUrl;
        model.lastMessage = lastMessageModel.content;
        model.id = lastMessageModel.conversationId;
        model.toID = lastMessageModel.toID;

        return model;

    }

    ListenerRegistration loadMessagesListener;

    ListenerRegistration adminListener;

    public void loadMessages(CollectionReference reference, String conversationID) {


        adminListener = reference.document(conversationID).collection("Conversations")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {

            List<MessageModel> messageModels = new ArrayList<>();

            if (value != null) {

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                    MessageModel messageModel = documentSnapshot.toObject(MessageModel.class);

                    messageModels.add(messageModel);

                }

                messagesData.postValue(new GenericModelLiveData(messageModels, GenericModelLiveData.Status.success, null));

            } else {

                messagesData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, error != null ? error.getMessage() : "Unable to load messages"));

            }

        });
    }

    public void deInit() {

        adminListener = null;

        loadMessagesListener = null;

    }


    public void checkAlreadyExists(ParcelableChat parcelableChat) {


        FirebaseInstances.chatCollection
                .whereEqualTo("participants." + parcelableChat.getSelectedUserId(), true)
                .get()
                .addOnCompleteListener(task -> {

                    final ArrayList<LastMessageModel> list = new ArrayList<>();

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                            LastMessageModel lastMessageModel = snapshot.get("lastMessage", LastMessageModel.class);

                            if(lastMessageModel != null) {

                                lastMessageModel.conversationId = snapshot.getId();

                                if (lastMessageModel.conversationId.equalsIgnoreCase(parcelableChat.getConversationID()))

                                    list.add(lastMessageModel);
                            }
                        }


                        if (list.size() > 0) {

                            for (int i = 0; i < list.size(); i++) {

                                if (list.get(i).getToID().equals(parcelableChat.getSelectedUserId()) || list.get(i).getFromID().equals(parcelableChat.getSelectedUserId())) {

                                    parcelableChat.setTypeStatus("false");

                                    getMyImage(parcelableChat);

                                    return;
                                }
                            }
                        }

                    } else {

                        if (task.getException() == null)

                            return;

                        if ((task.getException().getLocalizedMessage() != null))

                            alreadyExistsOrNotData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));
                    }

                });
    }

    ListenerRegistration eventListener;


    public void sendMessage(String s,String type, ParcelableChat parcelableChat) {

        if (parcelableChat.getTypeStatus().equals("true")) {

            long currentTimeStamp = System.currentTimeMillis();

            SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(s,

                    getUserId(), String.valueOf(currentTimeStamp), parcelableChat.getSelectedUserId(), type, false, (int) currentTimeStamp,parcelableChat.getProId());

            HashMap<String, Boolean> participents = new HashMap<>();

            participents.put(getUserId(), true);

            participents.put(parcelableChat.getSelectedUserId(), true);

            SendConversationModel sendConversationModel = new SendConversationModel(s,
                    getUserId(), String.valueOf(currentTimeStamp), type, false, currentTimeStamp, parcelableChat.getSelectedUserId(),parcelableChat.getProId());

            HashMap<String, Object> lasthashMap = new HashMap<>();

            lasthashMap.put("lastMessage", sendLastMessageModel);

            lasthashMap.put("participants", participents);

//            conversationID = UUID.randomUUID().toString();

            FirebaseInstances.chatCollection.document(parcelableChat.getConversationID()).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            FirebaseInstances.chatCollection.document(parcelableChat.getConversationID()).set(lasthashMap);

            loadMessages(FirebaseInstances.chatCollection, parcelableChat.getConversationID());



        } else {
            SendAlreadyExist(s,type, parcelableChat);
        }

        getUserData(parcelableChat.getSelectedUserId(),s);

        checkAlreadyExists(parcelableChat);

    }
    ListenerRegistration snapshotListener=null;
    private void getUserData(String id,String s) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("users").document(id);

        snapshotListener = reference.addSnapshotListener((value, error) -> {


            UserModel userModel = value.toObject(UserModel.class);
            sendNotification(userModel.userToken,s,id);

            snapshotListener.remove();



        });



    }

    private void sendNotification( String token,String m,String id) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "New Message")
                .addFormDataPart("description", m)
                .addFormDataPart("token_id", token)
                .addFormDataPart("user_id", id)
                .addFormDataPart("papi", id)
                .addFormDataPart("cnid", "")

                .build();
        NotificationController.getApi().setNotification(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });


    }




    public void SendAlreadyExist(String s,String type, ParcelableChat parcelableChat) {

        long currentTimeStamp = System.currentTimeMillis();
        SendConversationModel sendConversationModel = new SendConversationModel(s,
                getUserId(), String.valueOf(currentTimeStamp), type, false, currentTimeStamp, parcelableChat.getSelectedUserId(),parcelableChat.getProId());
        SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(s,
                getUserId(), String.valueOf(currentTimeStamp), parcelableChat.getSelectedUserId(), type, false, currentTimeStamp,parcelableChat.getProId());

        if (parcelableChat.getTypeStatus().equals("admin")) {

            firebaseFirestore.collection("AdminChats").document(parcelableChat.getConversationID()).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("AdminChats").document(parcelableChat.getConversationID()).update("lastMessage", sendLastMessageModel);
            loadMessages(firebaseFirestore.collection("AdminChats"), parcelableChat.getConversationID());

        } else {

            firebaseFirestore.collection("Chats").document(parcelableChat.getConversationID()).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("Chats").document(parcelableChat.getConversationID()).update("lastMessage", sendLastMessageModel);
            loadMessages(FirebaseInstances.chatCollection, parcelableChat.getConversationID());

        }
    }


    public void sendImageMessage(String s, ParcelableChat parcelableChat) {

        if (parcelableChat.getTypeStatus().equals("true")) {

            long currentTimeStamp = System.currentTimeMillis();

            SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(s,

                    getUserId(), String.valueOf(currentTimeStamp), parcelableChat.getSelectedUserId(), "image", false, (int) currentTimeStamp,parcelableChat.getProId());

            HashMap<String, Boolean> participents = new HashMap<>();

            participents.put(getUserId(), true);

            participents.put(parcelableChat.getSelectedUserId(), true);

            SendConversationModel sendConversationModel = new SendConversationModel(s,
                    getUserId(), String.valueOf(currentTimeStamp), "image", false, currentTimeStamp, parcelableChat.getSelectedUserId(),parcelableChat.getProId());

            HashMap<String, Object> lasthashMap = new HashMap<>();

            lasthashMap.put("lastMessage", sendLastMessageModel);

            lasthashMap.put("participants", participents);

//            conversationID = UUID.randomUUID().toString();

            FirebaseInstances.chatCollection.document(parcelableChat.getConversationID()).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            FirebaseInstances.chatCollection.document(parcelableChat.getConversationID()).set(lasthashMap);

            loadMessages(FirebaseInstances.chatCollection, parcelableChat.getConversationID());
        } else {
            SendImageAlreadyExist(s, parcelableChat);
        }



        checkAlreadyExists(parcelableChat);

    }

    public void SendImageAlreadyExist(String s, ParcelableChat parcelableChat) {

        long currentTimeStamp = System.currentTimeMillis();
        SendConversationModel sendConversationModel = new SendConversationModel(s,
                getUserId(), String.valueOf(currentTimeStamp), "image", false, currentTimeStamp, parcelableChat.getSelectedUserId(),parcelableChat.getProId());
        SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(s,
                getUserId(), String.valueOf(currentTimeStamp), parcelableChat.getSelectedUserId(), "image", false, currentTimeStamp,parcelableChat.getProId());

        if (parcelableChat.getTypeStatus().equals("admin")) {

            firebaseFirestore.collection("AdminChats").document(parcelableChat.getConversationID()).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("AdminChats").document(parcelableChat.getConversationID()).update("lastMessage", sendLastMessageModel);
            loadMessages(firebaseFirestore.collection("AdminChats"), parcelableChat.getConversationID());

        } else {

            firebaseFirestore.collection("Chats").document(parcelableChat.getConversationID()).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("Chats").document(parcelableChat.getConversationID()).update("lastMessage", sendLastMessageModel);
            loadMessages(FirebaseInstances.chatCollection, parcelableChat.getConversationID());

        }
    }

}
