import { ProductPicture } from '../../admin/model/product.model';

export interface CartItemAdd {
  publicId: string;
  quantity: number;
}

export interface CartItem {
  name: string;
  price: number;
  brand: string;
  picture: ProductPicture;
  publicId: string;
  quantity: number
}

export interface StripeSession {
  id: string;
}

export interface Cart {
  products: CartItem[];
}
