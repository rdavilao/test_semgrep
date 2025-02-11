import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { environment } from "src/environments/environment.prod";
import { Observable } from "rxjs";
import { EmailDTO } from "../model/emailDTO";

@Injectable({
  providedIn: "root",
})
export class MailService {
  private url = environment.host + environment.mailApiPath + "mail";

  httpOptions = {
    headers: new HttpHeaders({ "Content-Type": "application/json" }),
  };

  constructor(private http: HttpClient) {}

  sendEmail(emailDTO: EmailDTO): Observable<any> {
    return this.http.post<any>(`${this.url}/send`, emailDTO);
  }
}
