export type CastType = 'ACTOR' | 'DIRECTOR' | 'WRITER';

export interface ContentCastDto {
  id?: number;
  contentId: number;
  castId: number;
  role: CastType;
  castName?: string;
}
