import { Location } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { MailService } from "../services/mail.service";
import { EmailDTO } from "../model/emailDTO";

@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.scss"],
})
export class HomeComponent implements OnInit {
  focus;
  focus1;

  nameInput = "";
  emailInput = "";
  messageInput = "";

  emailLoaderVisible: boolean = false;

  constructor(
    private router: Router,
    public location: Location,
    private mailService: MailService
  ) {}

  ngOnInit() {
    const contactSection = document.getElementById("contact");
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        var titlee = this.location.prepareExternalUrl(this.location.path());
        if (!entry.isIntersecting && titlee.includes("#/home")) {
          history.replaceState(null, "", "#/home");
        }
      });
    });

    if (contactSection) {
      observer.observe(contactSection);
    }
  }

  navigateTo(route: string, fragment: string): void {
    if (fragment) {
      this.router.navigate([route], { fragment }).then(() => {
        const element = document.getElementById(fragment);
        if (element) {
          element.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      });
    } else {
      this.router.navigate([route]).then(() => {});
    }
  }

  validateContactInfo() {
    if (!this.validateNotJustBlanSpace(this.nameInput)) {
      return false;
    }
    if (
      !this.validateNotJustBlanSpace(this.emailInput) ||
      !this.validEmail(this.emailInput)
    ) {
      return false;
    }

    if (!this.validateNotJustBlanSpace(this.messageInput)) {
      return false;
    }

    return true;
  }

  validEmail(email: string) {
    const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return regex.test(email);
  }

  validateNotJustBlanSpace(input: string) {
    const trimmedValue = input.trim();
    if (trimmedValue !== "") {
      return true;
    } else {
      return false;
    }
  }

  sendEmail() {
    const emailDTO = new EmailDTO(
      this.nameInput,
      this.emailInput,
      this.messageInput
    );
    this.emailLoaderVisible = true;
    this.mailService.sendEmail(emailDTO).subscribe(
      (next) => {
        this.emailLoaderVisible = false;
        setTimeout(() => {
          alert(
            "Information sent successfully. We'll get in touch with you shortly to confirm receipt of information."
          );
          window.location.reload();
        }, 500);
      },
      (error) => {
        this.emailLoaderVisible = false;
        setTimeout(() => {
          alert(
            "Sorry, the information wasn't sent successfully. Please try again later."
          );
          window.location.reload();
        }, 500);
      }
    );
  }
}
