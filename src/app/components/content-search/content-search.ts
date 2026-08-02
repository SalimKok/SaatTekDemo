import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OmdbService } from '../../services/omdb';
import { ContentDto } from '../../models/content';

@Component({
  selector: 'app-content-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './content-search.html',
  styleUrl: './content-search.css',
})
export class ContentSearch {
  private omdbService = inject(OmdbService);
  private cdr = inject(ChangeDetectorRef);

  imdbId: string = ''; 
  movie: ContentDto | null = null;
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  
  onSearch(): void {
    if (!this.imdbId.trim()) return;

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.movie = null;
    this.cdr.detectChanges();

    this.omdbService.previewContent(this.imdbId.trim()).subscribe({
      next: (data) => {
        this.movie = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Movie not found or an error occurred!';
        this.loading = false;
        this.cdr.detectChanges();
        console.error(err);
      }
    });
  }

 
  onSave(): void {
    if (!this.movie) return;

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.omdbService.saveContent(this.movie).subscribe({
      next: (savedData) => {
        this.successMessage = `"${savedData.metadata.title}" and its cast members were successfully saved to the database!`;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'An error occurred while saving the movie!';
        this.loading = false;
        this.cdr.detectChanges();
        console.error(err);
      }
    });
  }
}
