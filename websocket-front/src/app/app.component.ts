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

  ngOnInit(): void {
    this.conectarWebSocket();
    this.receberMensagensAtualizacao();
  }

  private conectarWebSocket() {
    this.socket = new WebSocket(`ws://localhost:8080/chat`);
    this.socket.onopen = () => {
      alert("Conectado com Sucesso!");
    };
  }

  sendMessage() {
    const message = 'Mensagem do Front';
    if (this.socket != undefined) {
      this.socket.send(message);
      alert("Mensagem enviada  com Sucesso!");
    }
  }

  private receberMensagensAtualizacao() {
    if (this.socket != undefined) {
      this.socket.onmessage = (event) => {
        const data = event.data;
        if (event.type === 'message') {
          this.mensagensLista = [];
          this.mensagensLista.push(`${data}`);
          const mensagemDTO: MensagemDTO = JSON.parse(event.data);
          console.log(mensagemDTO);
        }
      };
    }
  }
}


