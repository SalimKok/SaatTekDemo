import { Component, inject, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { ContentService } from '../../services/content';
import { ContentDto } from '../../models/content';
import { EditModal } from '../edit-modal/edit-modal';

@Component({
  selector: 'app-content-list',
  standalone: true,
  imports: [CommonModule, EditModal],
  templateUrl: './content-list.html',
  styleUrl: './content-list.css',
})
export class ContentList implements OnInit, OnDestroy {
  private contentService = inject(ContentService);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();

  movies: ContentDto[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  currentPage: number = 0;
  pageSize: number = 8;
  totalPages: number = 0;
  totalElements: number = 0;

  ngOnInit(): void {
    this.fetchMovies(this.currentPage);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  fetchMovies(page: number = 0): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.contentService.getAllContents(page, this.pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.movies = response.content || [];
          this.currentPage = response.number;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to load movies from server!';
          this.loading = false;
          this.cdr.detectChanges();
          console.error(err);
        }
      });
  }

  onNextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.fetchMovies(this.currentPage + 1);
    }
  }

  onPrevPage(): void {
    if (this.currentPage > 0) {
      this.fetchMovies(this.currentPage - 1);
    }
  }

  onDelete(id: number | undefined): void {
    if (!id) return;
    if (!confirm('Are you sure you want to delete this content?')) return;

    this.contentService.deleteContent(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.fetchMovies(this.currentPage);
        },
        error: (err) => {
          alert('Failed to delete movie!');
          console.error(err);
        }
      });
  }

  selectedMovie: ContentDto | null = null;

  openEditModal(movie: ContentDto): void {
    this.selectedMovie = movie;
  }
  onModalClosed(): void {
    this.selectedMovie = null;
  }
  onMovieSaved(): void {
    this.selectedMovie = null;
    this.fetchMovies(this.currentPage);
  }
}