package io.fabricatedatelier.mayor.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CitizenData {

    private List<UUID> citizens = new ArrayList<>();
    @Nullable
    private BlockPos deskPos = null;
    private int taxAmount;
    private int taxInterval;
    private long taxTime;
    private int registrationFee;
    private List<UUID> taxPaidCitizens = new ArrayList<>();
    private List<UUID> taxUnpaidCitizens = new ArrayList<>();
    private List<UUID> requestCitizens = new ArrayList<>();

    public CitizenData(List<UUID> citizens, @Nullable BlockPos deskPos, int taxAmount, int taxInterval, long taxTime, int registrationFee, List<UUID> taxPaidCitizens, List<UUID> taxUnpaidCitizens, List<UUID> requestCitizens) {
        this.citizens = citizens;
        this.deskPos = deskPos;
        this.taxAmount = taxAmount;
        this.taxInterval = taxInterval;
        this.taxTime = taxTime;
        this.registrationFee = registrationFee;
        this.taxPaidCitizens = taxPaidCitizens;
        this.taxUnpaidCitizens = taxUnpaidCitizens;
        this.requestCitizens = requestCitizens;
    }

    public CitizenData(NbtCompound nbt) {
        this.citizens.clear();
        for (int i = 0; i < nbt.getInt("CitizenUuids"); i++) {
            this.citizens.add(nbt.getUuid("CitizenUuid" + i));
        }
        this.deskPos = NbtHelper.toBlockPos(nbt, "DeskPos").orElse(null);
        this.taxAmount = nbt.getInt("TaxAmount");
        this.taxInterval = nbt.getInt("TaxInterval");
        this.taxTime = nbt.getLong("TaxTime");
        this.registrationFee = nbt.getInt("RegistrationFee");
        this.taxPaidCitizens.clear();
        for (int i = 0; i < nbt.getInt("TaxPayedCitizenUuids"); i++) {
            this.taxPaidCitizens.add(nbt.getUuid("TaxPayedCitizenUuid" + i));
        }
        this.requestCitizens.clear();
        for (int i = 0; i < nbt.getInt("RequestCitizenUuids"); i++) {
            this.requestCitizens.add(nbt.getUuid("RequestCitizenUuid" + i));
        }
    }

    public void writeDataToNbt(NbtCompound nbt) {
        nbt.putInt("CitizenUuids", this.citizens.size());
        for (int i = 0; i < this.citizens.size(); i++) {
            nbt.putUuid("CitizenUuid" + i, this.citizens.get(i));
        }
        if (this.deskPos != null) {
            nbt.put("DeskPos", NbtHelper.fromBlockPos(this.deskPos));
        }
        nbt.putInt("TaxAmount", this.taxAmount);
        nbt.putInt("TaxInterval", this.taxInterval);
        nbt.putLong("TaxTime", this.taxTime);
        nbt.putInt("RegistrationFee", this.registrationFee);
        nbt.putInt("TaxPayedCitizenUuids", this.taxPaidCitizens.size());
        for (int i = 0; i < this.taxPaidCitizens.size(); i++) {
            nbt.putUuid("TaxPayedCitizenUuid" + i, this.taxPaidCitizens.get(i));
        }
        nbt.putInt("RequestCitizenUuids", this.requestCitizens.size());
        for (int i = 0; i < this.requestCitizens.size(); i++) {
            nbt.putUuid("RequestCitizenUuid" + i, this.requestCitizens.get(i));
        }
    }

    public NbtCompound writeDataToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("CitizenUuids", this.citizens.size());
        for (int i = 0; i < this.citizens.size(); i++) {
            nbt.putUuid("CitizenUuid" + i, this.citizens.get(i));
        }
        if (this.deskPos != null) {
            nbt.put("DeskPos", NbtHelper.fromBlockPos(this.deskPos));
        }
        nbt.putInt("TaxAmount", this.taxAmount);
        nbt.putLong("TaxTime", this.taxTime);
        nbt.putInt("RegistrationFee", this.registrationFee);
        nbt.putInt("TaxPayedCitizenUuids", this.taxPaidCitizens.size());
        for (int i = 0; i < this.taxPaidCitizens.size(); i++) {
            nbt.putUuid("TaxPayedCitizenUuid" + i, this.taxPaidCitizens.get(i));
        }
        nbt.putInt("RequestCitizenUuids", this.requestCitizens.size());
        for (int i = 0; i < this.requestCitizens.size(); i++) {
            nbt.putUuid("RequestCitizenUuid" + i, this.requestCitizens.get(i));
        }
        return nbt;
    }

    // Citizens
    public List<UUID> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<UUID> citizens) {
        this.citizens = citizens;
    }

    public void addCitizen(UUID citizen) {
        if (!this.citizens.contains(citizen)) {
            this.citizens.add(citizen);
        }
        if (this.requestCitizens.contains(citizen)) {
            this.requestCitizens.remove(citizen);
        }
    }

    // Don't forget to remove mayor if citizen is mayor
    public void removeCitizen(UUID citizen) {
        this.citizens.remove(citizen);
        if (this.taxPaidCitizens.contains(citizen)) {
            this.taxPaidCitizens.remove(citizen);
        }
        if(this.taxUnpaidCitizens.contains(citizen)){
            this.taxUnpaidCitizens.remove(citizen);
        }
    }

    // Desk Pos
    @Nullable
    public BlockPos getDeskPos() {
        return deskPos;
    }

    public void setDeskPos(@Nullable BlockPos deskPos) {
        this.deskPos = deskPos;
    }

    // Tax Amount
    public int getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(int taxAmount) {
        this.taxAmount = taxAmount;
    }

    // Tax Interval
    public int getTaxInterval() {
        return taxInterval;
    }

    public void setTaxInterval(int taxInterval) {
        this.taxInterval = taxInterval;
    }

    // Tax Time
    public long getTaxTime() {
        return taxTime;
    }

    public void setTaxTime(long taxTime) {
        this.taxTime = taxTime;
    }

    // Registration Fee
    public int getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(int registrationFee) {
        this.registrationFee = registrationFee;
    }

    // Tax Paid
    public List<UUID> getTaxPaidCitizens() {
        return taxPaidCitizens;
    }

    public void setTaxPaidCitizens(List<UUID> taxPaidCitizens) {
        this.taxPaidCitizens = taxPaidCitizens;
    }

    public void addTaxPaidCitizen(UUID citizen) {
        if (!this.taxPaidCitizens.contains(citizen)) {
            this.taxPaidCitizens.add(citizen);
        }
    }

    public void removeTaxPaidCitizen(UUID citizen) {
        this.taxPaidCitizens.remove(citizen);
    }

    // Tax Unpaid
    public List<UUID> getTaxUnpaidCitizens() {
        return taxUnpaidCitizens;
    }

    public void setTaxUnpaidCitizens(List<UUID> taxUnpaidCitizens) {
        this.taxUnpaidCitizens = taxUnpaidCitizens;
    }

    public void addTaxUnpaidCitizen(UUID citizen) {
        if (!this.taxUnpaidCitizens.contains(citizen)) {
            this.taxUnpaidCitizens.add(citizen);
        }
    }

    public void removeTaxUnpaidCitizen(UUID citizen) {
        this.taxUnpaidCitizens.remove(citizen);
    }

    // Requests
    public List<UUID> getRequestCitizens() {
        return requestCitizens;
    }

    public void setRequestCitizens(List<UUID> requestCitizens) {
        this.requestCitizens = requestCitizens;
    }

    public void addRequestCitizen(UUID citizen) {
        if (!this.requestCitizens.contains(citizen)) {
            this.requestCitizens.add(citizen);
        }
    }

    public void removeRequestCitizen(UUID citizen) {
        this.requestCitizens.remove(citizen);
    }
}
