import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeaturedComponent } from './featured/featured.component';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'ecom-home',
  standalone: true,
  imports: [CommonModule, FeaturedComponent, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {}
