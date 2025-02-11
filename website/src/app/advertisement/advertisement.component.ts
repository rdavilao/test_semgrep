import { Component, OnInit } from "@angular/core";
import { EventService } from "../services/event.service";
import * as CryptoJS from "crypto-js";

@Component({
  selector: "app-advertisement",
  templateUrl: "./advertisement.component.html",
  styleUrls: ["./advertisement.component.css"],
})
export class AdvertisementComponent implements OnInit {
  private secretKey: string = "b#3DR+@##79=sQihM29Q";

  currentEvents = [];
  currentEventsComplete = [];
  pastEvents = [];
  active = 1;
  constructor(private eventService: EventService) {}

  ngOnInit(): void {
    this.eventService.getCurrentEvents().subscribe({
      next: (info) => {
        this.currentEvents = info;
        this.currentEventsComplete = this.currentEvents;
        this.sortData(1);
      },
      error: (e) => {},
    });

    this.eventService.getPastEvents().subscribe({
      next: (info) => {
        this.pastEvents = info;
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

  formatDate(date: string) {
    const inputDate = new Date(date);

    const formatter = new Intl.DateTimeFormat("en-US", {
      weekday: "short",
      month: "long",
      day: "numeric",
      year: "numeric",
    });

    const formattedDate = formatter.format(inputDate);

    return formattedDate.replace(",", "");
  }

  isConnectedFromMobile() {
    return window.innerWidth <= 768;
  }

  sortData(initial: number) {
    switch (this.active + initial) {
      case 1:
        this.currentEvents.sort(
          (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
        );
        break;
      case 2:
        this.currentEvents.sort(
          (a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()
        );
        break;
    }
  }

  filterData(opc: string) {
    switch (opc) {
      case "All":
        this.currentEvents = this.currentEventsComplete;
        break;
      case "Today":
        this.currentEvents = this.currentEventsComplete.filter((event) =>
          this.isToday(event.date)
        );
        break;
      case "This week":
        this.currentEvents = this.currentEventsComplete.filter((event) =>
          this.isThisWeek(event.date)
        );
        break;
      case "This month":
        this.currentEvents = this.currentEventsComplete.filter((event) =>
          this.isThisMonth(event.date)
        );
        break;
    }
  }

  isToday(dateString: string): boolean {
    const inputDate = new Date(dateString);
    const today = new Date();

    return (
      inputDate.getDate() === today.getDate() &&
      inputDate.getMonth() === today.getMonth() &&
      inputDate.getFullYear() === today.getFullYear()
    );
  }

  isThisWeek(dateString: string): boolean {
    const inputDate = new Date(dateString);
    const today = new Date();

    const firstDayOfWeek = new Date(
      today.setDate(today.getDate() - today.getDay())
    );
    firstDayOfWeek.setHours(0, 0, 0, 0);

    const lastDayOfWeek = new Date(firstDayOfWeek);
    lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6);
    lastDayOfWeek.setHours(23, 59, 59, 999);

    return inputDate >= firstDayOfWeek && inputDate <= lastDayOfWeek;
  }

  isThisMonth(dateString: string): boolean {
    const inputDate = new Date(dateString);
    const today = new Date();

    return (
      inputDate.getMonth() === today.getMonth() &&
      inputDate.getFullYear() === today.getFullYear()
    );
  }
}
