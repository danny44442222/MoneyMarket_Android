package com.buzzware.monymarket.activities.messages.chatList.vm;



import static com.buzzware.monymarket.activities.BaseActivity.getUserId;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.activities.messages.chatList.mo.ConversationModel;
import com.buzzware.monymarket.activities.messages.chatList.mo.LastMessageModel;
import com.buzzware.monymarket.classes.FirebaseInstances;
import com.buzzware.monymarket.generic.GenericModelLiveData;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListViewModel extends ViewModel {

    private MutableLiveData<GenericModelLiveData> data;

    public LiveData<GenericModelLiveData> getChatListLiveData() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
    }

    public void getConversationsList(String chatType) {

        final ArrayList<LastMessageModel> list = new ArrayList<>();

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        FirebaseInstances.chatCollection
                .whereEqualTo("participants." + getUserId(), true)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                            LastMessageModel lastMessageModel = snapshot.get("lastMessage", LastMessageModel.class);

                            if(lastMessageModel != null) {

                                lastMessageModel.conversationId = snapshot.getId();

//                                if (chatType.equals("many")){
//                                    lastMessageModel.groupName = snapshot.getString("groupName");
//                                }

                                list.add(lastMessageModel);
                            }
                        }

                        filterList(list);

                    } else {

                        if (task.getException() == null)

                            return;

                        if ((task.getException().getLocalizedMessage() != null))

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                    }

                });

    }

    private void filterList(ArrayList<LastMessageModel> list) {

        List<ConversationModel> conversations = new ArrayList<>();

        FirebaseInstances.usersCollection
                . get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {

                        for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments()) {

                            UserModel user = null;

                            try {

                                user = documentSnapshot.toObject(UserModel.class);

                            } catch (Exception e) {

                            }

                            if(user != null) {

                                user.userId = documentSnapshot.getId();

                                for (int i = 0; i < list.size(); i++) {

                                    String otherUserId = list.get(i).fromID;

                                    if (getUserId().equalsIgnoreCase(list.get(i).fromID))

                                        otherUserId = list.get(i).toID;


                                    if (user.userId.equalsIgnoreCase(otherUserId)) {

                                        ConversationModel conversation = getConversationModel(list.get(i), user);

                                        if(user.onlineStatus)
                                            conversation.isActive=true;

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
        model.productId=lastMessageModel.productId;
        model.name = user.fullname;
//        if (lastMessageModel.groupName != null){
//            model.name = lastMessageModel.groupName;
//        }
        model.image = user.imageUrl;
        model.lastMessage = lastMessageModel.content;
        model.id = lastMessageModel.conversationId;
        model.toID = user.userId;

        return model;

    }

}
