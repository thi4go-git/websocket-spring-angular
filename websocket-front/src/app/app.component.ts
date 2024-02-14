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
  conectado: boolean = false;

  TOKEN: string = 'eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJY1ZyMHpVbUFuazM5WGRRcUhwcEJ2eWVfRWNlblRCQ0Y3RnFMblZDRmlJIn0.eyJleHAiOjE3MDc5MjA3NDYsImlhdCI6MTcwNzkxODk0NiwianRpIjoiODFhMzZkYTgtMDJjMC00YTU2LThiNWItNzQ0YzFjNWUxZTg1IiwiaXNzIjoiaHR0cDovL2Nsb3VkdGVjbm9sb2dpYS5keW5ucy5jb206ODE4MC9yZWFsbXMvQ0xPVURfVEVDTk9MT0dJQSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzNjRkMzEwOC04ODMxLTRiYmYtOGUyZS1hZmZjNTc2ZTI5OWQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJmbHV4by1jYWl4YS1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiOTI2YWY1YjItZGU0Yi00M2MzLWExYzktMzliZTBiMjkwYmJiIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWNsb3VkX3RlY25vbG9naWEiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiOTI2YWY1YjItZGU0Yi00M2MzLWExYzktMzliZTBiMjkwYmJiIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiVGhpYWdvIE1lbG8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0aGlhZ28ubWVsbyIsImdpdmVuX25hbWUiOiJUaGlhZ28iLCJmYW1pbHlfbmFtZSI6Ik1lbG8iLCJlbWFpbCI6InRoaTRnbzE5QGdtYWlsLmNvbSJ9.JtBzQd3xoU53FyoR7MjXQcsD8YUqHoNjGOZoe0jVCvCewPIp2iSOvGBf6id6tOPqhFwYeodPNc3mHPLNu_rOD1IQEv0SvjYqSsRMjuLmfDy3zkEdTC_XT8LdHXNAafL5E2yuuYRlr2W_P_V7A2ocYtytSqVmDGockkdrHWM7T8doyudpEa7gsDO2cMTACdsaO4njPpht1qNsEAGjnGv2K7uTi5idrkkxf1yQJp7KmtWpS2I7la9RZK8Eh8PCha6qsvRzQbesEC_fRCLSvU3CNMmdgnOSEev-Klv34mRcegIyftk-qal7HlHf9gZKavxDweLKam_Wl2HZKijnu-GWng';


  ngOnInit(): void {
    this.conectarWebSocketSimples();
    this.receberMensagensAtualizacao();
  }

  private conectarWebSocketSimples() {
    this.socket = new WebSocket(`ws://localhost:8080/chat?token=` + this.TOKEN);
    this.socket.onopen = () => {
      //Ao abrir conexão
      this.conectado = true;
    };
    this.socket.onclose = () => {
      this.conectado = false;
      alert("Conexão encerrada!");
    };
  }

  sendMessage() {
    const message = {
      "idSession": "203cae66-7c91-4657-bfa2-a613473de7e3",
      "msg": "MENSAGEM DO FRONTEND!"
    };
    if (this.socket != undefined) {
      this.socket.send(JSON.stringify(message));
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


