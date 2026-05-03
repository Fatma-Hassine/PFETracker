import { useEffect, useRef, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { Client, IMessage } from '@stomp/stompjs';

// useWebSocket.ts ligne 5
const WS_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v3')
  .replace('/api/v3', '') + '/ws';
export interface WebSocketMessage {
  type: string;
  payload: any;
  senderId?: number;
  receiverId?: number;
  timestamp: number;
}

interface UseWebSocketOptions {
  onMessage?: (msg: WebSocketMessage) => void;
  onNotification?: (msg: WebSocketMessage) => void;
  onTyping?: (msg: WebSocketMessage) => void;
  pfeId?: number;
}

export function useWebSocket(options: UseWebSocketOptions) {
  const clientRef = useRef<Client | null>(null);
  const isConnected = useRef(false);

  const getToken = () => localStorage.getItem('token');

  const connect = useCallback(() => {
    const token = getToken();
    if (!token || isConnected.current) return;

    console.log("Attempting WS connection to:", WS_URL);

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        isConnected.current = true;
        console.log("✅ WS connecté");

        // Subscribe to personal notification queue
        client.subscribe('/user/queue/notifications', (msg: IMessage) => {
          try {
            const data: WebSocketMessage = JSON.parse(msg.body);
            options.onNotification?.(data);
          } catch { }
        });

        // Subscribe to personal message queue
        client.subscribe('/user/queue/messages', (msg: IMessage) => {
          try {
            const data: WebSocketMessage = JSON.parse(msg.body);
            options.onMessage?.(data);
          } catch { }
        });

        // Subscribe to typing indicators
        client.subscribe('/user/queue/typing', (msg: IMessage) => {
          try {
            const data: WebSocketMessage = JSON.parse(msg.body);
            options.onTyping?.(data);
          } catch { }
        });

        // Subscribe to PFE-specific message topic if pfeId provided
        if (options.pfeId) {
          client.subscribe(`/topic/messages/${options.pfeId}`, (msg: IMessage) => {
            try {
              const data: WebSocketMessage = JSON.parse(msg.body);
              options.onMessage?.(data);
            } catch { }
          });
        }
      },
      onDisconnect: () => {
        isConnected.current = false;
        console.log("WS déconnecté");
      },
      onStompError: (e) => {
        isConnected.current = false;
        console.error("❌ WS erreur", e);
      },
    });

    client.activate();
    clientRef.current = client;
  }, [options.pfeId]);

  const disconnect = useCallback(() => {
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      isConnected.current = false;
    }
  }, []);

  const sendMessage = useCallback((pfeId: number, receiverId: number, content: string) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination: '/app/chat.send',
        body: JSON.stringify({ pfeId, receiverId, content }),
      });
    }
  }, []);

  const sendTyping = useCallback((receiverId: number, pfeId: number, isTyping: boolean) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination: '/app/chat.typing',
        body: JSON.stringify({ receiverId, pfeId, isTyping }),
      });
    }
  }, []);

  const markRead = useCallback((messageId: number) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination: '/app/chat.read',
        body: JSON.stringify(messageId),
      });
    }
  }, []);

  useEffect(() => {
    connect();
    
    const interval = setInterval(() => {
      if (!isConnected.current && getToken()) {
        connect();
      }
    }, 5000);

    return () => {
      clearInterval(interval);
      disconnect();
    };
  }, [connect, disconnect]);

  return { sendMessage, sendTyping, markRead, isConnected: isConnected.current };
}
