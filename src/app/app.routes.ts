import { Routes } from '@angular/router';
import { ContentSearch } from './components/content-search/content-search';
import { ContentList } from './components/content-list/content-list';

export const routes: Routes = [
    { path: '', redirectTo: 'movies', pathMatch: 'full' },
  { path: 'movies', component: ContentList },
  { path: 'search', component: ContentSearch }
];
