import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { environment } from "src/environments/environment.prod";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class BusinessService {
  private url = environment.host + environment.systemApiPath + "business";

  httpOptions = {
    headers: new HttpHeaders({ "Content-Type": "application/json" }),
  };

  constructor(private http: HttpClient) {}

  getAllBusinessInfo(): Observable<any> {
    return this.http.get<any>(`${this.url}/getAllBusinessInfo`);
  }

  getBusinessInfoById(id: number): Observable<any> {
    const params = new HttpParams().set("businessId", id);
    return this.http.get<any>(`${this.url}/getBusinessInfo`, {
      params: params,
    });
  }
}
