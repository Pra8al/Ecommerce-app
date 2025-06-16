import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { Oauth2Service } from '../../auth/oauth2.service';
import { RouterLink } from '@angular/router';
import { ClickOutside } from 'ngxtension/click-outside';
import { UserProductService } from '../../shared/service/user-product.service';
import { injectQuery } from '@tanstack/angular-query-experimental';
import { lastValueFrom } from 'rxjs';
import { CartService } from '../../shop/cart.service';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'ecom-navbar',
  standalone: true,
  imports: [CommonModule, FaIconComponent, RouterLink, ClickOutside],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent implements OnInit {
  oauth2Service = inject(Oauth2Service);
  productService = inject(UserProductService);
  connectedUserQuery = this.oauth2Service.connectedUserQuery;
  cartService = inject(CartService);

  numberOfItemsInCart = 0;

  categoryQuery = injectQuery(() => ({
    queryKey: ['categories'],
    queryFn: () => lastValueFrom(this.productService.findAllCategories()),
  }));

  ngOnInit(): void {
    this.listenToCart();
  }

  login(): void {
    this.closeDropDownMenu();
    this.oauth2Service.login();
  }

  logout(): void {
    this.closeDropDownMenu();
    this.oauth2Service.logout();
  }

  isConnected(): boolean {
    return (
      this.connectedUserQuery?.status() === 'success' &&
      this.connectedUserQuery?.data()?.email !== this.oauth2Service.notConnected
    );
  }

  closeDropDownMenu(): void {
    const bodyElement = document.activeElement as HTMLBodyElement;
    if (bodyElement) {
      bodyElement.blur();
    }
  }

  closeMenu(adminMenu: HTMLDetailsElement) {
    adminMenu.removeAttribute('open');
  }

  private listenToCart(): void {
    this.cartService.addedToCart.subscribe((productsInCart) => {
      this.numberOfItemsInCart = productsInCart.reduce(
        (acc, product) => acc + product.quantity,
        0
      );
    });
  }
}
