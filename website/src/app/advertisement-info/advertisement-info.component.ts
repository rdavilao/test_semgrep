import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import * as CryptoJS from "crypto-js";
import { EventService } from "../services/event.service";
import { CalendarService } from "../services/calendar.service";

@Component({
  selector: "app-advertisement-info",
  templateUrl: "./advertisement-info.component.html",
  styleUrls: ["./advertisement-info.component.css"],
})
export class AdvertisementInfoComponent implements OnInit {
  private secretKey: string = "b#3DR+@##79=sQihM29Q";
  eventInfo: any = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private calendarService: CalendarService
  ) {}

  ngOnInit(): void {
    this.eventInfo = {};
    this.route.paramMap.subscribe((params) => {
      let id = this.decryptData(params.get("id")).split("-")[0];
      this.eventService.getEventInfoById(Number(id)).subscribe(
        (next) => {
          this.eventInfo = next;
        },
        (error) => {
          this.router.navigate(["/advertisement"]);
        }
      );
    });
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

  formatDate(date: string) {
    const inputDate = new Date(date ? date : null);
    const formatter = new Intl.DateTimeFormat("en-US", {
      weekday: "long",
      month: "long",
      day: "numeric",
      year: "numeric",
    });

    const formattedDate = formatter.format(inputDate);

    return formattedDate.replace(",", "");
  }

  formatTime(date: string) {
    const inputDate = new Date(date ? date : null);

    const clientTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

    const formatter = new Intl.DateTimeFormat("en-US", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
      timeZone: clientTimeZone,
    });
    const formattedTime = formatter.format(inputDate).replace(/:/g, "h:") + "m";
    return formattedTime;
  }

  validateDate(date: string) {
    const inputDate = new Date(date);
    const now = new Date();
    return inputDate >= now;
  }

  setReminder(eventInfo: any) {
    const originalDate = new Date(eventInfo.date);
    const newDate = new Date(originalDate);
    newDate.setHours(originalDate.getHours() + 1);

    const event = {
      title: "PLCC Event: " + eventInfo.title,
      description: eventInfo.description,
      location: eventInfo.address,
      startDate: new Date(eventInfo.date),
      endDate: newDate,
    };
    const icsContent = this.calendarService.generateICSEvent(event);
    this.calendarService.downloadICSFile(icsContent, "evento.ics");
  }
}
