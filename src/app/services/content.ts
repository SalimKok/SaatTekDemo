import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContentDto } from '../models/content';
import { environment } from '../../environments/environment';


export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})

export class ContentService {
  private http = inject(HttpClient);
  private apiUrl = environment.url+'/contents';


  getAllContents(page = 0, size = 20): Observable<PageResponse<ContentDto>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<ContentDto>>(this.apiUrl, { params });
  }

  saveContent(content: ContentDto): Observable<ContentDto> {
    return this.http.post<ContentDto>(this.apiUrl, content);
  }

  deleteContent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateContent(id: number, content: ContentDto): Observable<ContentDto> {
  return this.http.put<ContentDto>(`${this.apiUrl}/${id}`, content);
  }
}
