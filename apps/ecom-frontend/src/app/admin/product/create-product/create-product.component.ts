import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminProductService } from '../../admin-product.service';
import { ToastService } from '../../../shared/toast/toast.service';
import { Router } from '@angular/router';
import {
  BaseProduct,
  CreateProductFormContent,
  ProductPicture,
  ProductSizes, sizes
} from '../../model/product.model';
import { injectMutation, injectQuery } from '@tanstack/angular-query-experimental';
import { lastValueFrom } from 'rxjs';
import { NgxControlError } from 'ngxtension/control-error';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'ecom-create-product',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgxControlError],
  templateUrl: './create-product.component.html',
  styleUrl: './create-product.component.scss',
})
export class CreateProductComponent {
  formBuilder = inject(FormBuilder);
  productService = inject(AdminProductService);
  toastService = inject(ToastService);
  router = inject(Router);

  productPictures = new Array<ProductPicture>();

  name = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  description = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  price = new FormControl<number>(0, {
    nonNullable: true,
    validators: [Validators.required],
  });
  size = new FormControl<ProductSizes>('M', {
    nonNullable: true,
    validators: [Validators.required],
  });
  category = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  brand = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  color = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  featured = new FormControl<boolean>(false, {
    nonNullable: true,
    validators: [Validators.required],
  });
  pictures = new FormControl<Array<ProductPicture>>([], {
    nonNullable: true,
    validators: [Validators.required],
  });
  stock = new FormControl<number>(0, {
    nonNullable: true,
    validators: [Validators.required],
  });

  createForm = this.formBuilder.nonNullable.group<CreateProductFormContent>({
    brand: this.brand,
    color: this.color,
    description: this.description,
    name: this.name,
    price: this.price,
    size: this.size,
    category: this.category,
    featured: this.featured,
    pictures: this.pictures,
    stock: this.stock,
  });

  loading = false;

  categoriesQuery = injectQuery(() => ({
    queryKey: ['categories'],
    queryFn: () => lastValueFrom(this.productService.findAllCategories()),
  }));

  createMutation = injectMutation(() => ({
    mutationFn: (product: BaseProduct) =>
      lastValueFrom(this.productService.createProduct(product)),
    onSuccess: () => this.onCreationSuccess(),
    onError: () => this.onCreationError(),
    onSettled: () => this.onCreationSettled(),
  }));

  create(): void {
    const productToCreate: BaseProduct = {
      brand: this.createForm.getRawValue().brand,
      color: this.createForm.getRawValue().color,
      description: this.createForm.getRawValue().description,
      name: this.createForm.getRawValue().name,
      price: this.createForm.getRawValue().price,
      size: this.createForm.getRawValue().size,
      category: {
        publicId: this.createForm.getRawValue().category.split('+')[0],
        name: this.createForm.getRawValue().category.split('+')[1],
      },
      featured: this.createForm.getRawValue().featured,
      pictures: this.productPictures,
      numberInStock: this.createForm.getRawValue().stock,
    };
    this.loading = true;
    this.createMutation.mutate(productToCreate);
  }

  private extractFileFromTarget(target: EventTarget | null): FileList | null {
    const htmlInputTarget = target as HTMLInputElement;
    if (htmlInputTarget === null || htmlInputTarget.files === null) {
      return null;
    }
    return htmlInputTarget.files;
  }

  onUploadNewPicture(target: EventTarget | null): void {
    this.productPictures = [];
    const pictureFileList = this.extractFileFromTarget(target);
    if (pictureFileList !== null) {
      for (let i = 0; i < pictureFileList.length; i++) {
        const picture = pictureFileList.item(i);
        if (picture !== null) {
          const productPicture: ProductPicture = {
            file: picture,
            mimeType: picture.type,
          };
          this.productPictures.push(productPicture);
        }
      }
    }
  }

  private onCreationSettled(): void {
    this.loading = false;
  }

  private onCreationSuccess(): void {
    this.router.navigate(['/admin/products/list']);
    this.toastService.show('Product created', 'SUCCESS');
  }

  private onCreationError(): void {
    this.toastService.show('Error on creating product', 'ERROR');
  }

  protected readonly sizes = sizes;
}
