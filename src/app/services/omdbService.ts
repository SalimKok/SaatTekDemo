import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContentDto } from '../models/content';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class OmdbService {
  private http = inject(HttpClient);
  private apiUrl = environment.url;

  previewContent(imdbId: string): Observable<ContentDto> {
    return this.http.get<ContentDto>(`${this.apiUrl}/omdb/preview?imdbId=${imdbId}`);
  }
}