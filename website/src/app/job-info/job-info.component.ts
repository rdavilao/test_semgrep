import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import * as CryptoJS from "crypto-js";
import { JobService } from "../services/job.service";

@Component({
  selector: "app-job-info",
  templateUrl: "./job-info.component.html",
  styleUrls: ["./job-info.component.css"],
})
export class JobInfoComponent implements OnInit {
  private secretKey: string = "b#3DR+@##79=sQihM29Q";
  businessInfo: any = [];
  jobInfo: any = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private jobService: JobService
  ) {}

  ngOnInit() {
    this.businessInfo = {};
    this.route.paramMap.subscribe((params) => {
      let id = this.decryptData(params.get("id")).split("-")[0];
      this.jobService.getJobInfoById(Number(id)).subscribe(
        (next) => {
          console.log(next);
          this.jobInfo = next;
          this.businessInfo = this.jobInfo.business;
        },
        (error) => {
          this.router.navigate(["/jobs"]);
        }
      );
    });
  }

  isConnectedFromMobile() {
    return window.innerWidth <= 768;
  }

  call(phoneNumber: string) {
    window.location.href = "tel:" + phoneNumber.replace(" ", "");
  }

  sendEmail(email: string, title: string) {
    window.location.href =
      "mailto:" + email + "?subject=Job Application: " + title;
  }

  base64UrlSafeDecode(str: string): string {
    str = str.replace(/-/g, "+").replace(/_/g, "/");
    while (str.length % 4) {
      str += "=";
    }
    return str;
  }

  decryptData(ciphertext: string): string {
    const base64Ciphertext = this.base64UrlSafeDecode(ciphertext);
    const bytes = CryptoJS.AES.decrypt(base64Ciphertext, this.secretKey);
    const originalData = bytes.toString(CryptoJS.enc.Utf8);
    return originalData;
  }
}
