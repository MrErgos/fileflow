const apiUrl = '/api/'
window.addEventListener('DOMContentLoaded', () => {
    fetch(apiUrl + 'me')
        .then(res => {
            if (res.ok) return res.json();
            throw new Error('Unauthorized')
        })
        .then(data => {
            showMainSection(data.login);
        })
        .catch(reason => {
            document.getElementById('authSection').style.display = 'block';
            console.log('Нужно авторизоваться')
        })
})
function auth(type) {
    const loginVal = document.getElementById('login').value;
    const passVal = document.getElementById('password').value;

    const formData = new URLSearchParams();
    formData.append('login', loginVal);
    formData.append('password', passVal);

    fetch(apiUrl + type, {
        method: 'POST',
        body: formData
    })
        .then(res => {
            if (res.ok) {
                showMainSection(loginVal);
            } else {
                alert("Ошибка: " + res.statusText);
            }
        });
}

function showMainSection(username) {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('mainSection').style.display = 'block';
    document.getElementById('userGreeting').innerText = "Привет, " + username;
}

function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];
    if (!file) return alert("Выбери файл!");

    const formData = new FormData();
    formData.append("newFile", file);

    const xhr = new XMLHttpRequest();
    const bar = document.getElementById('progressBar');
    const container = document.getElementById('progressContainer');

    container.classList.remove('progress-hidden');
    xhr.withCredentials = true;

    xhr.upload.onprogress = (e) => {
        if (e.lengthComputable) {
            const pct = Math.round((e.loaded / e.total) * 100);
            bar.style.width = pct + '%';
            bar.innerText = pct + '%';
        }
    };

    xhr.onload = () => {
        if (xhr.status === 200) {
            const data = JSON.parse(xhr.responseText);
            document.getElementById('result').innerHTML = `Ссылка: <a href="${data.location}" target="_blank">${data.location}</a>`;
        } else {
            alert("Ошибка загрузки!");
        }
    };

    xhr.open("POST", apiUrl + "upload");
    xhr.send(formData);
}

function logout() {
    fetch(apiUrl + 'logout', { method: 'POST' }).then(() => location.reload());
}