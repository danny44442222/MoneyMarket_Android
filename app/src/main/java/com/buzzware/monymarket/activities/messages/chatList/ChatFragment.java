package com.buzzware.monymarket.activities.messages.chatList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.activities.messages.chatList.adapter.ChatListAdapter;
import com.buzzware.monymarket.activities.messages.chatList.mo.ConversationModel;
import com.buzzware.monymarket.activities.messages.chatList.vm.ChatListViewModel;
import com.buzzware.monymarket.classes.OfferMessageEvent;
import com.buzzware.monymarket.databinding.FragmentChatBinding;
import com.buzzware.monymarket.fragments.BaseFragment;
import com.buzzware.monymarket.generic.GenericModelLiveData;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class ChatFragment extends BaseFragment {


    FragmentChatBinding binding;

    ChatListViewModel model;

    List<UserModel> chattedUsersList;
    long childCount;
    long count;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentChatBinding.inflate(inflater,container,false);
        EventBus.getDefault().post(new OfferMessageEvent(""));

        init();

        setViews();


        return binding.getRoot();
    }

    private void setViews() {

//        binding.includeView.endTV.setVisibility(View.VISIBLE);
//        binding.includeView.endTV.setText("Add New");
//        binding.includeView.endTV.setOnClickListener(view1 -> startActivity(new Intent(getContext(), AddChatActivity.class)));
//
//
//        binding.chatsTabLayout.addTab(binding.chatsTabLayout.newTab().setText("Chats"));
//        binding.chatsTabLayout.addTab(binding.chatsTabLayout.newTab().setText("Groups"));
//
//        binding.chatsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()) {
//                    case 0:
//                        model.getConversationsList("one");
//                        break;
//                    case 1:
//                        model.getConversationsList("many");
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });


        model.getConversationsList("");
    }

    void init() {

        setLiveData();

        initViews();
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {

        binding.titleTV.setText("Chat");

        binding.chatRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setLiveData() {

        model = new ViewModelProvider(this).get(ChatListViewModel.class);

        model.getChatListLiveData().observe(this, this::handleResponse);

        model.getConversationsList("one");
    }

    private void handleResponse(GenericModelLiveData genericModelLiveData) {

        switch (genericModelLiveData.status) {

            case error:

                hideLoader();

                showErrorAlert(genericModelLiveData.errorMsg);

                break;

            case loading:

                showLoader();

                break;

            case success:

                hideLoader();

                List<ConversationModel> conversations = (List<ConversationModel>) genericModelLiveData.object;

                if (conversations != null)

                    setChatListAdapter(conversations);

                break;
        }
    }

    private void setChatListAdapter(List<ConversationModel> conversations) {

        binding.chatRV.setAdapter(new ChatListAdapter(conversations, getActivity()));

    }


}