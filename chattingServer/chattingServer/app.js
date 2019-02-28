/*var app=require("express")(); //require메소드로 express 모듈을 생성해서 무언가를 가져온다
var http=require("http").Server(app); //프로토콜 정의 및 프레임 워크를 적용한다!
var io=require("socket.io")(http); //입출력 생성

//첫번째 매개변수 : url, 두번쨰 매개변수 처리할 함수, req res 는 서블릿 do get과 유사
app.get("/", function(req,res){
	console.log("hihi 너 지금 node 서버에 접속했네!");
	res.sendFile(__dirname+"/views/chat.html"); // express에서 지원하는 전역변수 __ 언더바 두개! __dirname의 의미는 루트 경로
});

http.listen("9000",function() {
	console.log("노드서버");
});
//포트번호 9000으로 서버 응답 설정

// 채팅메시지 구현~!!!
// io변수 넣어놓은 socket.io객체를 이용하여 처리
// 실시간으로 클라이언트를 연결해주는 역할(websocket)
// 메시지를 전송 : emit함수 이용!
// 메시지 수신 : on바인더를 이용
// io는 접속한 클라이언트의 별도의 session을 유지
// 접속된 session을 가지고 전체 통신을 진행!

//사용자가 접속하면 자동으로 
io.on("connection", function(socket){
	console.log("사용자 접속");
	console.log("접속자 아이디 : "+socket.id); //session
	
	//클라이언트가 보낸 플래그값(이벤트메시지) 중 message값과 연결이 됨
	socket.on("message",function(msg){
		console.log(msg);
		if(msg['msg']=="서블릿")
			{
				
			}
		
		io.emit("message",msg); //접속된 사용자 전체에게 msg전송
	});
});*/

var net = require('net');
var HOST = '127.0.0.1'; // parameterize the IP of the Listen
var PORT = 9080; // TCP LISTEN port

//Create an instance of the Server and waits for a conexão
net.createServer(function(sock) {
	sock.setEncoding("UTF-8");

//	Receives a connection - a socket object is associated to the connection automatically
	console.log('CONNECTED: ' + sock.remoteAddress +':'+ sock.remotePort);

//	Add a 'data' - "event handler" in this socket instance
	sock.on('data', function(data) {
//		data was received in the socket
//		Writes the received message back to the socket (echo)
		sock.write("답변이다 : "+data);
	});

//	Add a 'close' - "event handler" in this socket instance
	sock.on('close', function(data) {
//		closed connection
		console.log('CLOSED: ' + sock.remoteAddress +' '+ sock.remotePort);
	});
	
	sock.on('error', function(data) {
//		error connection
		console.log('ERROR: ' + sock.remoteAddress +' '+ sock.remotePort);
	});

}).listen(PORT, HOST);

console.log('Server listening on ' + HOST +':'+ PORT);