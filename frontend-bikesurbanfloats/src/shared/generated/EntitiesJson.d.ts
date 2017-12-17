/**
 * This file was automatically generated by json-schema-to-typescript.
 * DO NOT MODIFY IT BY HAND. Instead, modify the source JSONSchema file,
 * and run json-schema-to-typescript to regenerate this file.
 */

export interface EntitiesJson {
  users: {
    id: number;
    type: 'USER_TEST';
    walkingVelocity: number;
    cyclingVelocity: number;
  }[];
  bikes: {
    id: number;
  }[];
  stations: {
    id: number;
    position: {
      latitude: number;
      longitude: number;
    };
    capacity: number;
    bikes: (number | null)[];
  }[];
  reservations?: {
    id: number;
    startTime: number;
    user: number;
    station: number;
    bike?: number;
    type: 'SLOT' | 'BIKE';
    state: 'FAILED' | 'ACTIVE' | 'EXPIRED' | 'SUCCESSFUL';
  }[];
}