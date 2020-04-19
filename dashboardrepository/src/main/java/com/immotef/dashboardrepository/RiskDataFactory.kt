package com.immotef.dashboardrepository


import com.immotef.dashboardrepository.data.*

/**
 *
 */
internal interface RiskDataFactory {
    fun setFirstInfectionState(dashboardData: DashboardData)
    fun produce(dashboardData: DashboardData): InfectionState
}

internal class RiskDataFactoryImp : RiskDataFactory {
    private var oldInfectionState: InfectionState? = null

    override fun setFirstInfectionState(dashboardData: DashboardData) {
        oldInfectionState = produce(dashboardData)
    }

    override fun produce(dashboardData: DashboardData): InfectionState = when {
        dashboardData.isRecovered -> RecoveredState(
            oldColor = oldInfectionState?.colorTo ?: R.color.low_background,
            oldCircleColor = oldInfectionState?.circleColorTo ?: R.color.low_risk,
            riskColorFrom = oldInfectionState?.riskTextColor ?: R.color.low_risk,
            totalPeople = dashboardData.peoopleYouHaveMet,
            infectedPeople = dashboardData.infectedPeople,
            previousRotation = oldInfectionState?.rotation ?: 0
        )
        dashboardData.isInfected -> InfectedState(
            oldColor = oldInfectionState?.colorTo ?: R.color.low_background,
            oldCircleColor = oldInfectionState?.circleColorTo ?: R.color.low_risk,
            riskColorFrom = oldInfectionState?.riskTextColor ?: R.color.low_risk,
            totalPeople = dashboardData.peoopleYouHaveMet,
            infectedPeople = dashboardData.infectedPeople,
            previousRotation = oldInfectionState?.rotation ?: 0
        )
        dashboardData.risk in 0..33 -> LowRiskState(
            oldColor = oldInfectionState?.colorTo ?: R.color.low_background,
            oldCircleColor = oldInfectionState?.circleColorTo ?: R.color.low_risk,
            riskColorFrom = oldInfectionState?.riskTextColor ?: R.color.low_risk,
            totalPeople = dashboardData.peoopleYouHaveMet,
            infectedPeople = dashboardData.infectedPeople,
            previousRotation = oldInfectionState?.rotation ?: 0,
            riskLevel = 20
        )
        dashboardData.risk in 31..80 -> MediumRiskState(
            oldColor = oldInfectionState?.colorTo ?: R.color.low_background,
            oldCircleColor = oldInfectionState?.circleColorTo ?: R.color.low_risk,
            riskColorFrom = oldInfectionState?.riskTextColor ?: R.color.low_risk,
            totalPeople = dashboardData.peoopleYouHaveMet,
            infectedPeople = dashboardData.infectedPeople,
            previousRotation = oldInfectionState?.rotation ?: 0,
            riskLevel = dashboardData.risk
        )
        else -> HighRiskState(
            oldColor = oldInfectionState?.colorTo ?: R.color.low_background,
            oldCircleColor = oldInfectionState?.circleColorTo ?: R.color.low_risk,
            riskColorFrom = oldInfectionState?.riskTextColor ?: R.color.low_risk,
            totalPeople = dashboardData.peoopleYouHaveMet,
            infectedPeople = dashboardData.infectedPeople,
            previousRotation = oldInfectionState?.rotation ?: 0,
            riskLevel = 80
        )
    }.also {
        oldInfectionState = it
    }


}