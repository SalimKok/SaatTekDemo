import { Component, inject, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { ContentService } from '../../services/content';
import { ContentDto } from '../../models/content';

@Component({
  selector: 'app-content-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
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

  // Pagination state
  currentPage: number = 0;
  pageSize: number = 8;
  totalPages: number = 0;
  totalElements: number = 0;

  // Edit Modal State
  isEditModalOpen: boolean = false;
  editingMovie: ContentDto | null = null;
  editLoading: boolean = false;
  editErrorMessage: string = '';

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

  openEditModal(movie: ContentDto): void {
    this.editingMovie = JSON.parse(JSON.stringify(movie));
    if (!this.editingMovie?.metadata) {
      this.editingMovie!.metadata = {
        title: '',
        poster: '',
        plot: '',
        imdbRating: 0,
        genre: '',
        language: '',
        country: '',
        released: '',
        runtime: '',
      };
    }
    this.editErrorMessage = '';
    this.isEditModalOpen = true;
    this.cdr.detectChanges();
  }

  onContentTypeChange(newType: string): void {
    if (!this.editingMovie) return;
    if (newType !== 'SEASON' && newType !== 'EPISODE') {
      this.editingMovie.seasonNo = undefined as any;
      this.editingMovie.episodeNo = undefined as any;
    } else if (newType === 'SEASON') {
      this.editingMovie.episodeNo = undefined as any;
    }
  }

  closeEditModal(): void {
    this.isEditModalOpen = false;
    this.editLoading = false;
    this.editErrorMessage = '';
    this.cdr.detectChanges();
  }

  saveEditedMovie(): void {
    if (!this.editingMovie || !this.editingMovie.id) return;
    this.editLoading = true;
    this.editErrorMessage = '';
    this.cdr.detectChanges();

    const payload = {
      contentType: this.editingMovie.contentType,
      seasonNo: this.editingMovie.seasonNo,
      episodeNo: this.editingMovie.episodeNo,
      parentId: this.editingMovie.parentId,
      metadata: {
        title: this.editingMovie.metadata.title,
        poster: this.editingMovie.metadata.poster,
        plot: this.editingMovie.metadata.plot,
        imdbRating: Number(this.editingMovie.metadata.imdbRating) || 0,
        genre: this.editingMovie.metadata.genre,
        language: this.editingMovie.metadata.language,
        country: this.editingMovie.metadata.country,
        released: this.editingMovie.metadata.released,
        runtime: this.editingMovie.metadata.runtime,
        imdbVotes: this.editingMovie.metadata.imdbVotes,
        imdbID: this.editingMovie.metadata.imdbID
      },
      casts: (this.editingMovie.casts || []).map((c: any) => ({
        castId: c.cast?.id ?? c.castId ?? c.id,
        role: c.role,
        castName: c.cast?.name || c.castName
      }))
    };

    this.contentService.updateContent(this.editingMovie.id, payload as any)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.isEditModalOpen = false;
          this.editLoading = false;
          this.editingMovie = null;
          this.editErrorMessage = '';
          this.cdr.detectChanges();
          this.fetchMovies(this.currentPage);
        },
        error: (err) => {
          this.editErrorMessage = 'Update failed! Check console for details.';
          this.editLoading = false;
          this.cdr.detectChanges();
          console.error('Update error:', err);
        }
      });
  }
}