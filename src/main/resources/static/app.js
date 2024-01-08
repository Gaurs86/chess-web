const url = 'http://localhost:8080';
let stompClient;
let gameId;
let playerId;


 



function connectToSocket(gameId, callback) {

    console.log("connecting to the socket");
    const socket = new WebSocket('ws://localhost:8080/ws-chess');
    const stompClient = Stomp.over(socket);
    
   
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        
        stompClient.subscribe("/chess" + gameId, function (response) {
            let data = JSON.parse(response.body);
            console.log(data);
        })
        
        if (callback && typeof callback === 'function') {
            callback(); 
        }
        
        
    })
    
    
}

function create_game() {
	console.log("create game method called");
    let name = document.getElementById("name").value;
    if (name == null || name === '') {
        alert("Please enter name");
    } else {
		console.log("create game method called");
        $.ajax({
            url: url + "/create",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "playerId": name,
                "color": "WHITE"
            }),
            success: function (data) {
				console.log("create game response: "+data);
                gameId = data.gameId;
                client.activate();
                alert("Your created a game. Game id is: " + data.id);
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}



function connectToSpecificGame() {
	console.log("calling join api" );
    playerId = document.getElementById("name2").value;
    if (playerId == null || playerId === '') {
        alert("Please enter name");
    } else {
        gameId = document.getElementById("game_id").value;
        if (gameId == null || gameId === '') {
            alert("Please enter game id");
        }
        
        $.ajax({
			
            url: url + "/join",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            timeout: 5000,
            data: JSON.stringify({
				gameId: gameId,
                playerId: playerId 
            }),
            success: function (data) {	
				console.log("connected to the game");	
				console.log(data);
				
                connectToSocket(gameId);
                alert("Congrats you're playing with: " + data.players[0].playerId);
            },
            error: function (error) {
                console.log(error);
            }
        })
        
        
        
        
        
    }
}


function playChessMove() {
	
	
	
	if (stompClient && stompClient.connected) {
        console.log('StompClient is already connected');
        sendMessage(message);
    } else {
        console.log('StompClient is not connected. Connecting...');

        // Connect StompClient and execute the callback on successful connection
        connectToSocket(gameId, () => {
            console.log('Connected, sending message...');
            sendMessage(message);
        });
    }
    
    
    
    stompClient.send('/app/move/'+gameId, {}, JSON.stringify({
			"moveId": "1",
        	"playerId": "101",
        	"from" : {"x":"6","y":"3"},
        	"to": {"x":"4","y":"3"}
    	}));
	
	
}


function sendMessage(message) {
    // Replace '/app/your-destination' with your actual destination
    const destination = '/chess/' + gameId;

    stompClient.send(destination, {}, JSON.stringify({ content: message }));
}
