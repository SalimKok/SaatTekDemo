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
  private apiUrl = environment.Url;

  previewContent(imdbId: string): Observable<ContentDto> {
    return this.http.get<ContentDto>(`${this.apiUrl}/omdb/preview?imdbId=${imdbId}`);
  }
  saveContent(content: ContentDto): Observable<ContentDto> {
    return this.http.post<ContentDto>(`${this.apiUrl}/contents`, content);
  }
}