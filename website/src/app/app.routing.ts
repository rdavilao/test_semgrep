import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { BrowserModule } from "@angular/platform-browser";
import { Routes, RouterModule } from "@angular/router";

import { HomeComponent } from "./home/home.component";
import { ProfileComponent } from "./profile/profile.component";
import { DirectoryComponent } from "./directory/directory.component";
import { AboutUsComponent } from "./about-us/about-us.component";
import { AdvertisementComponent } from "./advertisement/advertisement.component";
import { AdvertisementInfoComponent } from "./advertisement-info/advertisement-info.component";
import { JobsComponent } from "./jobs/jobs.component";
import { JobInfoComponent } from "./job-info/job-info.component";
import { AgreementComponent } from "./agreement/agreement.component";

const routes: Routes = [
  { path: "home", component: HomeComponent },
  { path: "advertisement", component: AdvertisementComponent },
  { path: "advertisement/info/:id", component: AdvertisementInfoComponent },
  { path: "jobs", component: JobsComponent },
  { path: "jobs/info/:id", component: JobInfoComponent },
  { path: "directory/:id", component: DirectoryComponent },
  { path: "directory/info/:id", component: ProfileComponent },
  { path: "agreements", component: AgreementComponent },
  { path: "about-us", component: AboutUsComponent },
  { path: "", redirectTo: "home", pathMatch: "full" },
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes, {
      useHash: true,
      anchorScrolling: "enabled",
    }),
  ],
  exports: [],
})
export class AppRoutingModule {}
