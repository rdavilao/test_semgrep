import { Component, OnInit } from "@angular/core";
import { Router, NavigationEnd, NavigationStart } from "@angular/router";
import { Location, PopStateEvent } from "@angular/common";

@Component({
  selector: "app-navbar",
  templateUrl: "./navbar.component.html",
  styleUrls: ["./navbar.component.scss"],
})
export class NavbarComponent implements OnInit {
  public isCollapsed = true;
  private lastPoppedUrl: string;
  private yScrollStack: number[] = [];

  constructor(public location: Location, private router: Router) {}

  ngOnInit() {
    this.router.events.subscribe((event) => {
      this.isCollapsed = true;
      if (event instanceof NavigationStart) {
        if (event.url != this.lastPoppedUrl)
          this.yScrollStack.push(window.scrollY);
      } else if (event instanceof NavigationEnd) {
        if (event.url == this.lastPoppedUrl) {
          this.lastPoppedUrl = undefined;
          window.scrollTo(0, this.yScrollStack.pop());
        } else window.scrollTo(0, 0);
      }
    });
    this.location.subscribe((ev: PopStateEvent) => {
      this.lastPoppedUrl = ev.url;
    });
  }

  isConnectedFromMobile() {
    return window.innerWidth <= 768;
  }

  isHome() {
    var titlee = this.location.prepareExternalUrl(this.location.path());

    if (titlee.includes("#/home")) {
      return true;
    } else {
      return false;
    }
  }

  isResources() {
    var titlee = this.location.prepareExternalUrl(this.location.path());

    if (
      titlee.includes("#/advertisement") ||
      titlee.includes("#/jobs") ||
      titlee.includes("#/agreements")
    ) {
      return true;
    } else {
      return false;
    }
  }

  isAdvertisement() {
    var titlee = this.location.prepareExternalUrl(this.location.path());

    if (titlee.includes("#/advertisement")) {
      return true;
    } else {
      return false;
    }
  }

  isJobs() {
    var titlee = this.location.prepareExternalUrl(this.location.path());

    if (titlee.includes("#/jobs")) {
      return true;
    } else {
      return false;
    }
  }

  isAgreements() {
    var titlee = this.location.prepareExternalUrl(this.location.path());

    if (titlee.includes("#/agreements")) {
      return true;
    } else {
      return false;
    }
  }

  isDirectory() {
    var titlee = this.location.prepareExternalUrl(this.location.path());

    if (titlee.includes("#/directory/")) {
      return true;
    } else {
      return false;
    }
  }

  isAboutUs() {
    var titlee = this.location.prepareExternalUrl(this.location.path());
    if (titlee === "#/about-us") {
      return true;
    } else {
      return false;
    }
  }

  isDocumentation() {
    var titlee = this.location.prepareExternalUrl(this.location.path());
    if (titlee === "#/documentation") {
      return true;
    } else {
      return false;
    }
  }
}
