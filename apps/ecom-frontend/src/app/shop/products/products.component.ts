import { Component, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductsFilterComponent } from '../products-filter/products-filter.component';
import { injectQueryParams } from 'ngxtension/inject-query-params';
import { ProductFilter } from '../../admin/model/product.model';
import { UserProductService } from '../../shared/service/user-product.service';
import { Router } from '@angular/router';
import { ToastService } from '../../shared/toast/toast.service';
import { Pagination } from '../../shared/model/request.model';
import { injectQuery } from '@tanstack/angular-query-experimental';
import { lastValueFrom } from 'rxjs';
import { ProductCardComponent } from '../product-card/product-card.component';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'ecom-products',
  standalone: true,
  imports: [CommonModule, ProductsFilterComponent, ProductCardComponent],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss',
})
export class ProductsComponent {
  category = injectQueryParams('category');
  size = injectQueryParams('size');
  sort = injectQueryParams('sort');
  productService = inject(UserProductService);
  router = inject(Router);
  toastService = inject(ToastService);

  constructor() {
    effect(() => {
      this.handleFilteredProductsQueryError();
    });

    effect(() => {
      this.handleParametersChange();
    });
  }

  pageRequest: Pagination = {
    page: 0,
    size: 20,
    sort: ['createdDate,desc'],
  };

  filterProducts: ProductFilter = {
    category: this.category(),
    size: this.size() ? this.size()! : '',
    sort: [this.sort() ? this.sort()! : ''],
  };

  lastCategory = '';

  filterProductsQuery = injectQuery(() => ({
    queryKey: ['products', this.filterProducts],
    queryFn: () =>
      lastValueFrom(
        this.productService.filter(this.pageRequest, this.filterProducts)
      ),
  }));

  onFilterChange(filterProducts: ProductFilter) {
    filterProducts.category = this.category();
    this.filterProducts = filterProducts;
    this.pageRequest.sort = filterProducts.sort;
    this.router.navigate(['/products'], {
      queryParams: {
        ...filterProducts,
      },
    });
    this.filterProductsQuery.refetch();
  }

  private handleFilteredProductsQueryError() {
    if (this.filterProductsQuery.isError()) {
      this.toastService.show(
        'Error! Failed to load products, please try again',
        'ERROR'
      );
    }
  }

  private handleParametersChange() {
    if (this.category()) {
      if (this.lastCategory != this.category() && this.lastCategory !== '') {
        this.filterProducts = {
          category: this.category(),
          size: this.size() ? this.size()! : '',
          sort: [this.sort() ? this.sort()! : ''],
        };
        this.filterProductsQuery.refetch();
      }
    }
    this.lastCategory = this.category()!;
  }
}
