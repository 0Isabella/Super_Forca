package com.example.forca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.forca.ui.screens.TelaAdmin
import com.example.forca.ui.screens.TelaAdminRanking
import com.example.forca.ui.screens.TelaHistorico
import com.example.forca.ui.screens.TelaInicio
import com.example.forca.ui.screens.TelaJogo
import com.example.forca.viewmodel.RankingViewModel

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val rankingViewModel: RankingViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rotas.inicio,
        modifier = modifier
    ) {
        composable(Rotas.inicio) {
            TelaInicio(
                onJogar = { navController.navigate(Rotas.jogo) },
                onAdmin = { navController.navigate(Rotas.admin) },
                onHistorico = { navController.navigate(Rotas.historico) }
            )
        }
        composable(Rotas.jogo) {
            TelaJogo(
                onFimDeJogo = { iniciais, pontos, tema ->
                    rankingViewModel.salvarPartida(iniciais, pontos, tema)
                    navController.navigate(Rotas.historico) {
                        popUpTo(Rotas.inicio)
                    }
                }
            )
        }
        composable(Rotas.historico) { TelaHistorico(viewModel = rankingViewModel) }
        composable(Rotas.admin) {
            TelaAdmin(
                onVoltar = { navController.popBackStack() },
                onAbrirRanking = { navController.navigate(Rotas.adminRanking) }
            )
        }
        composable(Rotas.adminRanking) {
            TelaAdminRanking(onVoltar = { navController.popBackStack() })
        }
    }
}