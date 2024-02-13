import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MensagemDTO } from './dto/mensagemDTO';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  title = 'websocket-front';
  socket: WebSocket | undefined;
  mensagensLista: string[] = [];
  private permiteConexaoWebSocket: boolean = true;

  ngOnInit(): void {
    this.conectarWebSocket();
    this.receberMensagensAtualizacao();
  }

  private conectarWebSocket() {
    this.socket = new WebSocket(`ws://localhost:8080/chat?conecta=` + this.permiteConexaoWebSocket);
    this.socket.onopen = () => {
      //alert("Conectado com Sucesso!");
    };

    this.socket.onclose = () => {
      alert("Conexão encerrada com Sucesso!");
    };
  }

  sendMessage() {
    const message = 'Mensagem do Front';
    if (this.socket != undefined) {
      this.socket.send(message);
    }
  }

  private receberMensagensAtualizacao() {
    if (this.socket != undefined) {
      this.socket.onmessage = (event) => {
        const data = event.data;
        if (event.type === 'message') {
          this.mensagensLista.push(`${data}`);
          const mensagemDTO: MensagemDTO = JSON.parse(event.data);
          console.log(mensagemDTO);
        }
      };
    }
  }
}


