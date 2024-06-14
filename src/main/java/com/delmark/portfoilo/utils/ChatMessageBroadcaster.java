package com.delmark.portfoilo.utils;
import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.user.User;
import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * <p><Broadcaster реализован по следующему паттерну:</p>
 * <a href>https://vaadin.com/docs/latest/flow/advanced/server-push#push.broadcaster</a>
 * <p> Пока не нашёл наиболее лучших вариантов достучаться до Vaadin'ского веб-сокета
 * или асинхронной работы между клиентами.
 * Как вариант брокеры сообщений? </p>*/
public class ChatMessageBroadcaster {
    private static final Map<Long, Set<Consumer<Chat>>> listeners = new HashMap<>();

    public static synchronized Registration registerListener(Long chatId, Consumer<Chat> listener) {
        // Заносим пользователя в слушатели
        listeners.computeIfAbsent(chatId, k -> new CopyOnWriteArraySet<>()).add(listener);

        // Срабатывает при Registration.remove()
        return () -> {
            Set<Consumer<Chat>> chatListeners = listeners.get(chatId);
            if (chatListeners != null) {
                chatListeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcastChatUpdate(Chat chat) {
        // Получаем всех участников чата в котором произошло обновление
        Set<User> chatUsers = chat.getUsers();
        for (User user : chatUsers) {
            // Получаем всех слушателей пользователя, в одном чате
            Set<Consumer<Chat>> userListeners = listeners.get(user.getId());
            if (userListeners != null) {
                // Рассылаем всем пользователям уведомление об обновлении
                for (Consumer<Chat> listener : userListeners) {
                    listener.accept(chat);
                }
            }
        }
    }
}

