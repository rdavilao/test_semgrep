import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import * as CryptoJS from "crypto-js";
import { BusinessService } from "../services/business.service";

@Component({
  selector: "app-profile",
  templateUrl: "./profile.component.html",
  styleUrls: ["./profile.component.scss"],
})
export class ProfileComponent implements OnInit {
  private secretKey: string = "b#3DR+@##79=sQihM29Q";
  businessInfo: any = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private businessService: BusinessService
  ) {}

  ngOnInit() {
    this.businessInfo = {};
    this.route.paramMap.subscribe((params) => {
      let id = this.decryptData(params.get("id")).split("-")[0];
      this.businessService.getBusinessInfoById(Number(id)).subscribe(
        (next) => {
          this.businessInfo = next;
        },
        (error) => {
          this.router.navigate(["/directory/A"]);
        }
      );
    });
  }

  call(phoneNumber: string) {
    window.location.href = "tel:" + phoneNumber.replace(" ", "");
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
