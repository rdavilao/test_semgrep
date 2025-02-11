import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class CalendarService {
  generateICSEvent(event: {
    title: string;
    description: string;
    location: string;
    startDate: Date;
    endDate: Date;
  }) {
    const { title, description, location, startDate, endDate } = event;

    const formatDate = (date: Date) =>
      date.toISOString().replace(/[-:]/g, "").split(".")[0] + "Z";

    const icsContent = `
BEGIN:VCALENDAR
VERSION:2.0
CALSCALE:GREGORIAN
BEGIN:VEVENT
SUMMARY:${title}
DESCRIPTION:${description}
LOCATION:${location}
DTSTART:${formatDate(startDate)}
DTEND:${formatDate(endDate)}
END:VEVENT
END:VCALENDAR
`.trim();

    return icsContent;
  }

  downloadICSFile(content: string, fileName: string) {
    const blob = new Blob([content], { type: "text/calendar" });
    const url = window.URL.createObjectURL(blob);
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = fileName;
    anchor.click();
    window.URL.revokeObjectURL(url);
  }
}
