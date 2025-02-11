import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { RouterModule } from "@angular/router";
import { AppRoutingModule } from "./app.routing";

import { AppComponent } from "./app.component";
import { ProfileComponent } from "./profile/profile.component";
import { HomeComponent } from "./home/home.component";
import { NavbarComponent } from "./shared/navbar/navbar.component";
import { FooterComponent } from "./shared/footer/footer.component";

import { DirectoryComponent } from "./directory/directory.component";
import { HttpClientModule } from "@angular/common/http";
import { AdvertisementComponent } from "./advertisement/advertisement.component";
import { AdvertisementInfoComponent } from "./advertisement-info/advertisement-info.component";
import { JobsComponent } from "./jobs/jobs.component";
import { JobInfoComponent } from "./job-info/job-info.component";
import { AgreementComponent } from "./agreement/agreement.component";
import { AboutUsComponent } from "./about-us/about-us.component";

@NgModule({
  declarations: [
    AppComponent,
    ProfileComponent,
    NavbarComponent,
    FooterComponent,
    HomeComponent,
    AboutUsComponent,
    DirectoryComponent,
    AdvertisementComponent,
    AdvertisementInfoComponent,
    JobsComponent,
    JobInfoComponent,
    AgreementComponent,
  ],
  imports: [
    BrowserModule,
    NgbModule,
    FormsModule,
    RouterModule,
    AppRoutingModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
