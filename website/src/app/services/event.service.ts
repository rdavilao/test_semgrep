import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { environment } from "src/environments/environment.prod";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class EventService {
  private url = environment.host + environment.systemApiPath + "event";

  httpOptions = {
    headers: new HttpHeaders({ "Content-Type": "application/json" }),
  };

  constructor(private http: HttpClient) {}

  getCurrentEvents(): Observable<any> {
    return this.http.get<any>(`${this.url}/getCurrentEvents`);
  }

  getPastEvents(): Observable<any> {
    return this.http.get<any>(`${this.url}/getPastEvents`);
  }

  getEventInfoById(id: number): Observable<any> {
    const params = new HttpParams().set("eventId", id);
    return this.http.get<any>(`${this.url}/getEventInfo`, {
      params: params,
    });
  }
}