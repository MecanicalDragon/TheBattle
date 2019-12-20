const appApi = DEPLOYED_URL;

export async function getSquad() {
    return fetch(appApi + 'squad/getNewSquad').then(function (response) {
        return response.json();
    }).then(resp => {
        if (resp.status !== undefined) {
            console.log(resp);
            return null;
        } else {
            return resp;
        }
    });
}