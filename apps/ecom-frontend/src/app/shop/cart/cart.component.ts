import { Component, effect, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { CartService } from '../cart.service';
import { Oauth2Service } from '../../auth/oauth2.service';
import { ToastService } from '../../shared/toast/toast.service';
import {
  injectMutation,
  injectQuery,
} from '@tanstack/angular-query-experimental';
import { lastValueFrom } from 'rxjs';
import {
  CartItem,
  CartItemAdd,
  StripeSession,
} from '../../shared/model/cart.model';
import { RouterLink } from '@angular/router';
import { StripeService } from 'ngx-stripe';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'ecom-cart',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent implements OnInit {
  cartService = inject(CartService);
  oauth2Service = inject(Oauth2Service);
  toastService = inject(ToastService);
  platformId = inject(PLATFORM_ID);

  labelCheckout = 'Login to checkout';

  action: 'login' | 'checkout' = 'login';

  cart: Array<CartItem> = [];

  isInitPaymentSessionLoading = false;

  stripeService = inject(StripeService);

  cartQuery = injectQuery(() => ({
    queryKey: ['cart'],
    queryFn: () => lastValueFrom(this.cartService.getCartDetails()),
  }));

  initPaymentSession = injectMutation(() => ({
    mutationFn: (cart: Array<CartItemAdd>) =>
      lastValueFrom(this.cartService.initPaymentSession(cart)),
    onSuccess: (result: StripeSession) => this.onSessionCreateSuccess(result),
  }));

  ngOnInit(): void {
    this.cartService.addedToCart.subscribe((cart) => {
      this.updateQuantity(cart);
    });
  }

  constructor() {
    this.extractListToUpdate();
    this.checkUserLoggedIn();
  }

  private checkUserLoggedIn(): void {
    effect(() => {
      const connectedUserQuery = this.oauth2Service.connectedUserQuery;
      if (connectedUserQuery?.isError()) {
        this.labelCheckout = 'Login to checkout';
        this.action = 'login';
      } else if (connectedUserQuery?.isSuccess()) {
        this.labelCheckout = 'Checkout';
        this.action = 'checkout';
      }
    });
  }

  private extractListToUpdate(): void {
    effect(() => {
      if (this.cartQuery.isSuccess()) {
        this.cart = this.cartQuery.data().products;
      }
    });
  }

  private updateQuantity(cartUpdated: Array<CartItemAdd>): void {
    for (const cartItemToUpdate of this.cart) {
      const itemToUpdate = cartUpdated.find(
        (item) => item.publicId === cartItemToUpdate.publicId
      );
      if (itemToUpdate) {
        cartItemToUpdate.quantity = itemToUpdate.quantity;
      } else {
        this.cart.splice(this.cart.indexOf(cartItemToUpdate), 1);
      }
    }
  }

  addQuantityToCart(publicId: string): void {
    this.cartService.addToCart(publicId, 'add');
  }

  removeQuantityToCart(publicId: string, quantity: number): void {
    if (quantity > 1) this.cartService.addToCart(publicId, 'remove');
  }

  removeItem(publicId: string): void {
    const itemIndex = this.cart.findIndex((item) => item.publicId === publicId);
    if (itemIndex >= 0) {
      this.cart.splice(itemIndex, 1);
    }
    this.cartService.removeFromCart(publicId);
  }

  computeTotal(): number {
    return this.cart.reduce((acc, item) => {
      return acc + item.quantity * item.price;
    }, 0);
  }

  checkIfEmptyCart(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return (
        this.cartQuery.isSuccess() &&
        this.cartQuery.data().products.length === 0
      );
    } else {
      return false;
    }
  }

  checkout() {
    if (this.action === 'login') {
      this.oauth2Service.login();
    } else if (this.action === 'checkout') {
      this.isInitPaymentSessionLoading = true;
      const cartItemsAdd = this.cart.map(
        (item) =>
          ({ publicId: item.publicId, quantity: item.quantity } as CartItemAdd)
      );
      this.initPaymentSession.mutate(cartItemsAdd);
    }
  }

  private onSessionCreateSuccess(sessionId: StripeSession) {
    this.cartService.storeSessionId(sessionId.id);
    this.stripeService
      .redirectToCheckout({ sessionId: sessionId.id })
      .subscribe((results) => {
        this.isInitPaymentSessionLoading = false;
        this.toastService.show(`Order error ${results.error.message}`, 'ERROR');
      });
  }
}
