import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { RouterModule } from '@angular/router';
import { fontAwesomeIcons } from './shared/font-awesome-icons';
import {
  FaConfig,
  FaIconComponent,
  FaIconLibrary,
} from '@fortawesome/angular-fontawesome';
import { NavbarComponent } from './layout/navbar/navbar.component';
import { FooterComponent } from './layout/footer/footer.component';
import { Oauth2Service } from './auth/oauth2.service';
import { isPlatformBrowser, NgClass } from '@angular/common';
import { ToastService } from './shared/toast/toast.service';

@Component({
  standalone: true,
  imports: [
    RouterModule,
    NavbarComponent,
    FooterComponent,
    NgClass,
    FaIconComponent,
  ],
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'ecom-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  private faIconLibrary = inject(FaIconLibrary);
  private faConfig = inject(FaConfig);
  private oauth2Service = inject(Oauth2Service);
  plaformId = inject(PLATFORM_ID);
  toastService = inject(ToastService);

  constructor() {
    if (isPlatformBrowser(this.plaformId)) {
      this.oauth2Service.initAuthentication();
    }
    this.oauth2Service.connectedUserQuery = this.oauth2Service.fetch();
  }

  ngOnInit(): void {
    this.initFontAwesome();
  }

  private initFontAwesome() {
    this.faConfig.defaultPrefix = 'far';
    this.faIconLibrary.addIcons(...fontAwesomeIcons);
  }
}
