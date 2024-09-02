import socket
import errno

HOST = '127.0.0.1'
PORT = 65432

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    print(f"Server listening at -> {HOST}:{PORT}")

    while True:
        try:
            connection, addr = s.accept()
            with connection:
                print(f"Connected with {addr}")
                while True:
                    data = connection.recv(1024)
                    if not data:
                        break
                    message_received = data.decode()
                    print(f"Received: {message_received}")

                    answer = "Message received successfully"
                    connection.sendall(answer.encode())
        except socket.error as e:
            if e.errno == errno.WSAECONNRESET: 
                print(f"Connection forcibly closed by the client: {addr}")
            else:
                print(f"Socket error: {e}")
        except Exception as e:
            print(f"Unexpected error: {e}")
        finally:
            if 'connection' in locals():
                connection.close()
                print(f"Connection with {addr} closed.")
