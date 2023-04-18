import {createRouter, createWebHistory} from "vue-router"
import HomeView from "../views/HomeView.vue"
import Article from "../components/MiniArticle.vue"
import CurrenciesTest from "../components/Currencies.vue"
import EditView from "../views/EditView.vue"
import UserPanelView from "@/views/UserPanelView.vue";
import BuisnessView from "@/views/kategories/BuisnessView.vue";
import LoginView from "@/views/LoginView.vue";

// -artykuly i artykul- do usuniecia mozna przekierowac do 404 za pomoca useRouter np gdy dane zapytanie nie na wynikow 
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: 'home',
            component: HomeView
        },
        {
            path: "/biznes",
            component: BuisnessView
        },
        {
            path: "/:loc?-artykuly",
            component: HomeView,
        },
        {
            path: "/edit",
            component: EditView,
        },
        {
            path: "/userpanel",
            component: UserPanelView,
        },
        {
            path: "/:loc?-artykuly/artykul?:subloc",
            component: Article,
        },
        {
            path: "/login",
            component: LoginView,
        },
        {
            path: '/404', 
            name: 'NotFound',
            component: {
                template: '<p>Page Not Found</p>'
            }
        },
        { 
            path: '/:pathMatch(.*)*', 
            name: 'DoesntExist',
            redirect: '404'
        }
    ]
})

export default router