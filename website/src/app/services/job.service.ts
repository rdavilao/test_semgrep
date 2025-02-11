import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { environment } from "src/environments/environment.prod";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class JobService {
  private url = environment.host + environment.systemApiPath + "job";

  httpOptions = {
    headers: new HttpHeaders({ "Content-Type": "application/json" }),
  };

  constructor(private http: HttpClient) {}

  getCurrentJobs(): Observable<any> {
    return this.http.get<any>(`${this.url}/getCurrentJobs`);
  }

  getAllJobsClassifications(): Observable<any> {
    return this.http.get<any>(`${this.url}/getJobsClassification`);
  }

  getJobInfoById(id: number): Observable<any> {
    const params = new HttpParams().set("jobId", id);
    return this.http.get<any>(`${this.url}/getJobInfo`, {
      params: params,
    });
  }
}
