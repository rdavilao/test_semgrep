import { Component, OnInit } from "@angular/core";
import { Router, ActivatedRoute } from "@angular/router";
import { BusinessService } from "../services/business.service";
import * as CryptoJS from "crypto-js";

@Component({
  selector: "app-directory",
  templateUrl: "./directory.component.html",
  styleUrls: ["./directory.component.css"],
})
export class DirectoryComponent implements OnInit {
  private secretKey: string = "b#3DR+@##79=sQihM29Q";

  focus: any;
  focus1: any;
  page = 2;
  paginationCollectionSize: number = 0;
  searchInput: string = "";
  currentUrl: string = "";
  allLetters = [
    "0-9",
    "A",
    "B",
    "C",
    "D",
    "E",
    "F",
    "G",
    "H",
    "I",
    "J",
    "K",
    "L",
    "M",
    "N",
    "O",
    "P",
    "Q",
    "R",
    "S",
    "T",
    "U",
    "V",
    "W",
    "X",
    "Y",
    "Z",
  ];

  allBusinessInformation = [];
  showedBusinessInformation = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private businessService: BusinessService
  ) {}

  ngOnInit(): void {
    this.paginationCollectionSize = 270;
    this.businessService.getAllBusinessInfo().subscribe({
      next: (info) => {
        this.allBusinessInformation = info;
        this.route.paramMap.subscribe((params) => {
          this.manageParams(params.get("id"));
        });
      },
      error: (e) => {},
    });
    this.route.paramMap.subscribe((params) => {
      this.manageParams(params.get("id"));
    });
  }

  encryptData(id: any, title: string): string {
    let data = String(id) +'-'+title;
    const ciphertext = CryptoJS.AES.encrypt(data, this.secretKey).toString();
    const urlSafeCiphertext = this.base64UrlSafeEncode(ciphertext);
    return urlSafeCiphertext;
  }

  base64UrlSafeEncode(str: string): string {
    return str.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
  }

  manageParams(id: string) {
    if (id) {
      let firstLetter = id.length > 1 ? id.charAt(0) : id;
      this.page = this.allLetters.indexOf(firstLetter) + 1;
      this.filterBusinessInformation(id);
      if (this.allBusinessInformation.length == 0) {
      }
    }
  }

  filterBusinessInformation(searchText: string) {
    this.showedBusinessInformation = this.allBusinessInformation.filter(
      (data) => data.title.toUpperCase().startsWith(searchText)
    );
  }

  call(phoneNumber: string) {
    window.location.href = "tel:" + phoneNumber.replace(" ", "");
  }

  searchInputOnChange(event: any) {
    this.searchInput = this.searchInput.toLocaleUpperCase();
    if (this.searchInput === "") {
      this.router.navigate(["/directory/A"]);
    } else {
      this.router.navigate(["/directory/" + this.searchInput]);
    }
  }

  getPageSymbol(current: number) {
    return this.allLetters[current - 1];
  }

  onPageChange(event: any) {
    this.router.navigate(["/directory/" + this.allLetters[this.page - 1]]);
  }
}
