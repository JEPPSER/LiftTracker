import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class FormService {

	constructor(
		private http: HttpClient
	) {}

	getFormContent(formGuid: string, filter: string = null): Observable<any> {
		let headers = new HttpHeaders();
		//headers = headers.set('Authorization', 'Bearer ' + this.authService.token);
		return this.http.get('https://colixpluginsformsapi.azurewebsites.net/api/v2/formcontent/' + formGuid + '?includes=Values' + (filter ? '&filter=' + filter : ''), { headers: headers });
	}
}
