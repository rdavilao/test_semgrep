import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-about-us",
  templateUrl: "./about-us.component.html",
  styleUrls: ["./about-us.component.css"],
})
export class AboutUsComponent implements OnInit {
  focus;
  focus1;

  constructor() {}

  ngOnInit(): void {
    var nucleoView = document.getElementsByClassName("icons-container")[0];
    window.addEventListener(
      "scroll",
      function (event) {
        if (this.isInViewport(nucleoView)) {
          nucleoView.classList.add("on-screen");
        } else {
          nucleoView.classList.remove("on-screen");
        }
      }.bind(this),
      false
    );
  }
  isInViewport(elem) {
    var bounding = elem.getBoundingClientRect();
    return (
      bounding.top >= 0 &&
      bounding.left >= 0 &&
      bounding.bottom <=
        (window.innerHeight || document.documentElement.clientHeight) &&
      bounding.right <=
        (window.innerWidth || document.documentElement.clientWidth)
    );
  }
}
