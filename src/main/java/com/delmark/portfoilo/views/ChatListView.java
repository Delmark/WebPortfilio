package com.delmark.portfoilo.views;

import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.messages.Message;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.service.interfaces.ChatService;
import com.delmark.portfoilo.service.interfaces.MessageService;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.delmark.portfoilo.utils.ChatMessageBroadcaster;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIcon;


import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

@Route(value = "chats", layout = MainLayout.class)
@PermitAll
@Slf4j
public class ChatListView extends HorizontalLayout {
    private List<Chat> chatList;
    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;
    private VerticalLayout openedChatLayout = null;
    private VerticalLayout chatListLayout = null;
    private VerticalLayout messageListLayout;
    private Registration broadcast;

    // TODO: Убрать хардкод пагинации, написать скроллеры
    public ChatListView(
            UserService userService,
            ChatService chatService,
            MessageService messageService
    ) {
        this.userService = userService;
        this.chatService = chatService;
        this.messageService = messageService;
        setMaxHeight("100%");
        setMaxWidth("100%");
        add(createChatList());
    }

    public VerticalLayout createChatList() {
        VerticalLayout chatList = new VerticalLayout();
        chatList.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
        chatListLayout = chatList;
        chatList.setWidth("100%");
        chatList.setHeight("100%");

        // TODO: Переписать в будущем
        this.chatList = chatService.getAllUserChats(userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication()).getUsername(), 0, 10).getContent();

        if (this.chatList.isEmpty()) {
            chatList.add(new H1("Вы ещё ни с кем не разговаривали!"));
        }
        else {
            chatList.add(new H3("Ваши чаты:"));

            for (Chat chatForInsert : this.chatList) {
                Div chatCard = new Div();
                chatCard.addClassNames(
                        LumoUtility.Display.FLEX,
                        LumoUtility.FlexDirection.ROW,
                        LumoUtility.Margin.SMALL,
                        LumoUtility.AlignItems.CENTER,
                        LumoUtility.Background.CONTRAST_5,
                        LumoUtility.Padding.SMALL,
                        LumoUtility.BorderRadius.MEDIUM
                );
                chatCard.setWidth("50%");

                Avatar chatAvatar = new Avatar();
                chatAvatar.setThemeName(AvatarVariant.LUMO_LARGE.getVariantName());

                List<Message> messagesFromChat = chatService.getMessagesByChatId(chatForInsert.getId(), 0, 10).getContent();
                String lastMessage;
                if (messagesFromChat.isEmpty()) {
                    chatAvatar.setImageResource(
                            new StreamResource("chat_avatar.png",
                                    () -> new ByteArrayInputStream(
                                            userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication()).getAvatar()
                                    ))
                    );
                    lastMessage = "Вы ещё ничего не написали...";
                }
                else {
                    chatAvatar.setImageResource(
                            new StreamResource("chat_avatar.png",
                                    () -> new ByteArrayInputStream(
                                            messagesFromChat.getLast().getSender().getAvatar()
                                    ))
                    );
                    lastMessage = messagesFromChat.getLast().getMessage();
                }
                chatCard.add(chatAvatar);

                VerticalLayout chatInfo = new VerticalLayout();
                chatInfo.setPadding(false);
                Span chatName = new Span(chatForInsert.getChatName());
                chatName.addClassNames(LumoUtility.TextColor.PRIMARY);
                Span messagePreview = new Span(lastMessage);
                messagePreview.addClassNames(LumoUtility.TextColor.SECONDARY);
                chatInfo.add(chatName, messagePreview);
                chatCard.add(chatInfo);

                chatCard.addClickListener(divClickEvent -> {
                    openChatPage(chatForInsert);
                });

                chatList.add(chatCard);
            }
        }

        return chatList;
    }

    public void openChatPage(Chat chat) {
        if (openedChatLayout == null) {
            openedChatLayout = createChatPage(chat);
            chatListLayout.setWidth("50%");
            add(openedChatLayout);
        }
        else {
            closeChatPage();
            openChatPage(chat);
        }
    }

    public void closeChatPage() {
        remove(openedChatLayout);
        messageListLayout = null;
        openedChatLayout = null;
        chatListLayout.setWidth("100%");
    }

    public VerticalLayout createChatPage(Chat chat) {
        VerticalLayout chatPage = new VerticalLayout();
        chatPage.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Border.LEFT, LumoUtility.BorderColor.CONTRAST_5);
        chatPage.setPadding(false);

        HorizontalLayout chatHeader = new HorizontalLayout();
        chatHeader.setPadding(false);
        chatHeader.setWidth("100%");
        chatHeader.setClassName(LumoUtility.Background.CONTRAST_5);
        chatHeader.add(new H3(chat.getChatName()));
        chatPage.add(chatHeader);

        List<Message> messages = chatService.getMessagesByChatId(chat.getId(), 0, 10).getContent();
        messageListLayout = new VerticalLayout();
        messageListLayout.setWidth("60%");
        messageListLayout.setPadding(false);
        for (Message message : messages) {
            messageListLayout.add(createMessageCard(message));
        }
        Scroller messageScroller = new Scroller(messageListLayout);
        messageScroller.setWidth("100%");
        messageScroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        chatPage.add(messageScroller);

        HorizontalLayout messageForm = createMessageForm(chat);
        messageForm.setPadding(false);
        chatPage.add(messageForm);
        return chatPage;
    }

    private HorizontalLayout createMessageForm(Chat chat) {
        HorizontalLayout messageForm = new HorizontalLayout();
        messageForm.setWidth("100%");
        TextArea textArea = new TextArea();
        textArea.setPlaceholder("...");
        textArea.setWidth("80%");
        Button sendButton = new Button("Отправить сообщение", event -> {
            if (!textArea.getValue().isBlank()) {
                messageService.createMessage(chat.getId(), textArea.getValue());
                ChatMessageBroadcaster.broadcastChatUpdate(chat);
            }
            else {
                Notification.show("Нельзя отправить пустое сообщение", 1000, Notification.Position.TOP_CENTER);
            }
        });
        sendButton.addClassNames(LumoUtility.Margin.SMALL);
        messageForm.add(textArea, sendButton);
        return messageForm;
    }

    private HorizontalLayout createMessageCard(Message message) {
        HorizontalLayout messageCard = new HorizontalLayout();
        messageCard.addClassNames(LumoUtility.Margin.MEDIUM, LumoUtility.Padding.SMALL);

        Avatar messagerAvatar = new Avatar();
        messagerAvatar.setImageResource(
                new StreamResource(message.getSender().getUsername() + "_avatar.png",
                        () -> new ByteArrayInputStream(message.getSender().getAvatar()))
        );
        messagerAvatar.setThemeName(AvatarVariant.LUMO_XLARGE.getVariantName());
        messageCard.add(messagerAvatar);
        messageCard.setWidth("100%");
        messageCard.setHeight("100%");

        VerticalLayout messageDataLayout = new VerticalLayout();
        messageDataLayout.setPadding(false);

        Anchor userLink = new Anchor();
        userLink.setHref("portfolio/" + message.getSender().getUsername());
        userLink.setText(message.getSender().getUsername() + "  " + message.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")));

        Span messageText = new Span(message.getMessage());
        messageDataLayout.add(userLink, messageText);
        messageCard.add(messageDataLayout);
        return messageCard;
    }

    @Override
    public void onAttach(AttachEvent event) {
        UI ui = event.getUI();
        broadcast = ChatMessageBroadcaster.registerListener(
                userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication()).getId(),
                chat -> {
                    ui.access(() -> {
                        log.info("Chat update!");
                        messageListLayout.removeAll();
                        List<Message> messages = chatService.getMessagesByChatId(chat.getId(), 0, 10).getContent();
                        for (Message message : messages) {
                            messageListLayout.add(createMessageCard(message));
                        }
                    });
                });
    }

    @Override
    public void onDetach(DetachEvent event) {;
        broadcast.remove();
        broadcast = null;
    }
}
