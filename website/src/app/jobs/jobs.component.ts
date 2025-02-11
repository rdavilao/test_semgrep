import { Component, OnInit } from "@angular/core";
import { JobService } from "../services/job.service";
import * as CryptoJS from "crypto-js";
import { Router } from "@angular/router";

@Component({
  selector: "app-jobs",
  templateUrl: "./jobs.component.html",
  styleUrls: ["./jobs.component.css"],
})
export class JobsComponent implements OnInit {
  private secretKey: string = "b#3DR+@##79=sQihM29Q";

  focus: any;
  active = 1;
  searchInput: string = "";
  currentJobs = [];
  currentJobsComplete = [];
  jobsClassifications = [];
  currentTypeFilter = "";
  previousCurrentTypeFilter = "";
  currentClassificationFilter = "";
  previousCurrentClassificationFilter = "";

  constructor(private router: Router, private jobService: JobService) {}

  ngOnInit(): void {
    this.jobService.getCurrentJobs().subscribe({
      next: (info) => {
        this.currentJobs = info;
        this.currentJobsComplete = this.currentJobs;
      },
      error: (e) => {},
    });
    this.jobService.getAllJobsClassifications().subscribe({
      next: (data) => {
        this.jobsClassifications = data;
      },
      error: (e) => {},
    });
  }

  encryptData(id: any, title: string): string {
    let data = String(id) + "-" + title;
    const ciphertext = CryptoJS.AES.encrypt(data, this.secretKey).toString();
    const urlSafeCiphertext = this.base64UrlSafeEncode(ciphertext);
    return urlSafeCiphertext;
  }

  base64UrlSafeEncode(str: string): string {
    return str.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
  }

  isConnectedFromMobile() {
    return window.innerWidth <= 768;
  }

  filterDataType(opc: string) {
    this.currentTypeFilter = opc;
    this.currentJobs = this.currentJobsComplete
      .filter((job) => job.type === opc || opc === "All" || opc === "")
      .filter(
        (job) =>
          job.classification === this.currentClassificationFilter ||
          this.currentClassificationFilter === "All" ||
          this.currentClassificationFilter === ""
      )
      .filter(
        (job) =>
          job.title.toLocaleUpperCase().includes(this.searchInput) ||
          job.business.title.toLocaleUpperCase().includes(this.searchInput) ||
          this.searchInput === ""
      );
  }

  filterDataClassification(opc: string) {
    this.currentClassificationFilter = opc;
    this.currentTypeFilter = "All";
    switch (opc) {
      case "All":
        this.currentTypeFilter = "All";
        this.currentJobs = this.currentJobsComplete;
        this.searchInputOnChange(null);
        break;
      default:
        this.currentJobs = this.currentJobsComplete
          .filter((job) => job.classification === opc)
          .filter(
            (job) =>
              job.type === this.currentTypeFilter ||
              this.currentTypeFilter === "All" ||
              this.currentTypeFilter === ""
          )
          .filter(
            (job) =>
              job.title.toLocaleUpperCase().includes(this.searchInput) ||
              job.business.title
                .toLocaleUpperCase()
                .includes(this.searchInput) ||
              this.searchInput === ""
          );
        break;
    }
  }

  searchInputOnChange(event: any) {
    this.currentTypeFilter = "All";
    this.currentClassificationFilter = "All";
    this.searchInput = this.searchInput.toLocaleUpperCase();
    if (this.searchInput === "") {
      this.currentJobs = this.currentJobsComplete;
    } else {
      this.currentJobs = this.currentJobsComplete.filter(
        (job) =>
          job.title.toLocaleUpperCase().includes(this.searchInput) ||
          job.business.title.toLocaleUpperCase().includes(this.searchInput)
      );
    }
  }
}
