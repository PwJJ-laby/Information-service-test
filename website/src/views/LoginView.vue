<script setup>
import TheTitle from "@/components/main-layout/TheTitle.vue";
import Cookie from "js-cookie";
import {useRouter} from 'vue-router'
// import axios from 'axios';
// export default {
//   name: "LoginView",
//   methods: {list},
//   components: {TheTitle}
// }

  const router = useRouter()

  //TODO: change it to request to backend instead of fake
  const login = async () =>{
    const username = document.querySelector('input[name="username"]').value;
    const password = document.querySelector('input[name="pass"]').value;
    let requestBody = JSON.stringify({
            username: username,
            password: password,
          })
  
    console.log(requestBody);
    // make request to backend
    try {
      const url = 'http://localhost:8080/client/login/v2';

      const data = `{
          "username": "Tom",
          "password": "Tom123"
      }`;

      const response = await fetch(url, {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json',
          },
          body: data,
      });

      const text = await response.text();

      console.log(text);
      // const response = await fetch('http://localhost:8080/client/login/v2', {
      //     method: 'POST',
      //     headers: {
      //       'Content-Type': 'application/json',
      //     },
      //     body: requestBody,
      //   });
      //   console.log("hejo")
      //   // If the request is not successful, throw an error
      //   // if (!response.ok) {
      //   //   throw new Error(await response.text());
      //   // }

      //   // // Parse the response JSON and store the token in local storage
      //   // const data = await response.json();
      //   console.log(response);
      //   localStorage.setItem('token', data.token);

        // Redirect to the dashboard page
        // router.push('/dashboard');
    } catch (error) {
      console.log(error);
    }

    // Do something with the username and password values
    if (username == "admin"){
      Cookie.set('role', 'admin')
      router.push('/')
    }

    if (username == "journalist"){
      Cookie.set('role', 'journalist')
      router.push('/')
    }

    if (username == "redactor"){
      Cookie.set('role', 'redactor')
      router.push('/')
    }

    if (username == "corrector"){
      Cookie.set('role', 'corrector')
      router.push('/')
    }

      // window.location.href = '/home';
    }

    if (username == "user"){
      Cookie.set('role', 'user')
      router.push('/')
      // window.location.href = '/home';
    }


    console.log(username, password);
  }
  
</script>

<template>
  <body>
  <div class="limiter">
    <img alt="Vue logo" class="logo" src="../assets/globe.png" width="50" height="50"/>
    <div class="wrapper">
      <TheTitle msg="Serwis informacyjny" />
    </div>
    <div class="container-login100">
      <div class="wrap-login100">
        <div class="login100-pic js-tilt" data-tilt>
          <img src="../../public/img-01.png" alt="IMG">
        </div>

        <form class="login100-form validate-form"  @submit.prevent>
					<span class="login100-form-title">
						Logowanie
					</span>

          <div class="wrap-input100 validate-input" data-validate = "Valid username is required: ex@abc.xyz">
            <input class="input100" type="text" name="username" placeholder="Login">
            <span class="focus-input100"></span>
            <span class="symbol-input100">
							<i class="fa fa-envelope" aria-hidden="true"></i>
						</span>
          </div>

          <div class="wrap-input100 validate-input" data-validate = "Password is required">
            <input class="input100" type="password" name="pass" placeholder="Hasło">
            <span class="focus-input100"></span>
            <span class="symbol-input100">
							<i class="fa fa-lock" aria-hidden="true"></i>
						</span>
          </div>

          <div class="container-login100-form-btn" >
            <button class="login100-form-btn" @click="login()">
              Zaloguj się
            </button>
          </div>
          <div class="container-login100-form-btn container-login200-form-btn">
            <button class="login100-form-btn login200-form-btn">
              <img src="../../public/google_icon.png" height="48" width="48"/>
              Zaloguj się przez Google
            </button>
          </div>
          <br>
          <div class="text-center p-t-136">
            <span class="txt1">
              Nie masz jeszcze konta?
						</span>
            <a class="txt2" href="/register">
              Zarejestruj się
            </a>
          </div>
          <div class="text-center p-t-136">
            <a class="txt2" href="/">
              Powrót na stronę główną
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>
  </body>
</template>

<style scoped>
@import '../assets/login.css';
</style>