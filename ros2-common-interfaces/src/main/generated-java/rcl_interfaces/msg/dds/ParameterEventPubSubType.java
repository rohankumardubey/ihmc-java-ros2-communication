package rcl_interfaces.msg.dds;

/**
 * Topic data type of the struct "ParameterEvent" defined in "ParameterEvent_.idl". Use this class to provide the TopicDataType to a Participant.
 *
 * This file was automatically generated from ParameterEvent_.idl by us.ihmc.idl.generator.IDLGenerator.
 * Do not update this file directly, edit ParameterEvent_.idl instead.
 */
public class ParameterEventPubSubType implements us.ihmc.pubsub.TopicDataType<rcl_interfaces.msg.dds.ParameterEvent>
{
   public static final java.lang.String name = "rcl_interfaces::msg::dds_::ParameterEvent_";
   private final us.ihmc.idl.CDR serializeCDR = new us.ihmc.idl.CDR();
   private final us.ihmc.idl.CDR deserializeCDR = new us.ihmc.idl.CDR();

   public ParameterEventPubSubType()
   {

   }

   public static int getMaxCdrSerializedSize()
   {
      return getMaxCdrSerializedSize(0);
   }

   public static int getMaxCdrSerializedSize(int current_alignment)
   {
      int initial_alignment = current_alignment;

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);
      for (int a = 0; a < 100; ++a)
      {
         current_alignment += rcl_interfaces.msg.dds.ParameterPubSubType.getMaxCdrSerializedSize(current_alignment);
      }

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);
      for (int a = 0; a < 100; ++a)
      {
         current_alignment += rcl_interfaces.msg.dds.ParameterPubSubType.getMaxCdrSerializedSize(current_alignment);
      }

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);
      for (int a = 0; a < 100; ++a)
      {
         current_alignment += rcl_interfaces.msg.dds.ParameterPubSubType.getMaxCdrSerializedSize(current_alignment);
      }

      return current_alignment - initial_alignment;
   }

   public final static int getCdrSerializedSize(rcl_interfaces.msg.dds.ParameterEvent data)
   {
      return getCdrSerializedSize(data, 0);
   }

   public final static int getCdrSerializedSize(rcl_interfaces.msg.dds.ParameterEvent data, int current_alignment)
   {
      int initial_alignment = current_alignment;

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);
      for (int a = 0; a < data.getNewParameters().size(); ++a)
      {
         current_alignment += rcl_interfaces.msg.dds.ParameterPubSubType.getCdrSerializedSize(data.getNewParameters().get(a), current_alignment);
      }

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);
      for (int a = 0; a < data.getChangedParameters().size(); ++a)
      {
         current_alignment += rcl_interfaces.msg.dds.ParameterPubSubType.getCdrSerializedSize(data.getChangedParameters().get(a), current_alignment);
      }

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4);
      for (int a = 0; a < data.getDeletedParameters().size(); ++a)
      {
         current_alignment += rcl_interfaces.msg.dds.ParameterPubSubType.getCdrSerializedSize(data.getDeletedParameters().get(a), current_alignment);
      }

      return current_alignment - initial_alignment;
   }

   public static void write(rcl_interfaces.msg.dds.ParameterEvent data, us.ihmc.idl.CDR cdr)
   {

      if (data.getNewParameters().size() <= 100)
         cdr.write_type_e(data.getNewParameters());
      else
         throw new RuntimeException("new_parameters field exceeds the maximum length");

      if (data.getChangedParameters().size() <= 100)
         cdr.write_type_e(data.getChangedParameters());
      else
         throw new RuntimeException("changed_parameters field exceeds the maximum length");

      if (data.getDeletedParameters().size() <= 100)
         cdr.write_type_e(data.getDeletedParameters());
      else
         throw new RuntimeException("deleted_parameters field exceeds the maximum length");
   }

   public static void read(rcl_interfaces.msg.dds.ParameterEvent data, us.ihmc.idl.CDR cdr)
   {

      cdr.read_type_e(data.getNewParameters());

      cdr.read_type_e(data.getChangedParameters());

      cdr.read_type_e(data.getDeletedParameters());
   }

   public static void staticCopy(rcl_interfaces.msg.dds.ParameterEvent src, rcl_interfaces.msg.dds.ParameterEvent dest)
   {
      dest.set(src);
   }

   @Override
   public void serialize(rcl_interfaces.msg.dds.ParameterEvent data, us.ihmc.pubsub.common.SerializedPayload serializedPayload) throws java.io.IOException
   {
      serializeCDR.serialize(serializedPayload);
      write(data, serializeCDR);
      serializeCDR.finishSerialize();
   }

   @Override
   public void deserialize(us.ihmc.pubsub.common.SerializedPayload serializedPayload, rcl_interfaces.msg.dds.ParameterEvent data) throws java.io.IOException
   {
      deserializeCDR.deserialize(serializedPayload);
      read(data, deserializeCDR);
      deserializeCDR.finishDeserialize();
   }

   @Override
   public final void serialize(rcl_interfaces.msg.dds.ParameterEvent data, us.ihmc.idl.InterchangeSerializer ser)
   {
      ser.write_type_e("new_parameters", data.getNewParameters());

      ser.write_type_e("changed_parameters", data.getChangedParameters());

      ser.write_type_e("deleted_parameters", data.getDeletedParameters());
   }

   @Override
   public final void deserialize(us.ihmc.idl.InterchangeSerializer ser, rcl_interfaces.msg.dds.ParameterEvent data)
   {
      ser.read_type_e("new_parameters", data.getNewParameters());

      ser.read_type_e("changed_parameters", data.getChangedParameters());

      ser.read_type_e("deleted_parameters", data.getDeletedParameters());
   }

   @Override
   public rcl_interfaces.msg.dds.ParameterEvent createData()
   {
      return new rcl_interfaces.msg.dds.ParameterEvent();
   }

   @Override
   public int getTypeSize()
   {
      return us.ihmc.idl.CDR.getTypeSize(getMaxCdrSerializedSize());
   }

   @Override
   public java.lang.String getName()
   {
      return name;
   }

   public void serialize(rcl_interfaces.msg.dds.ParameterEvent data, us.ihmc.idl.CDR cdr)
   {
      write(data, cdr);
   }

   public void deserialize(rcl_interfaces.msg.dds.ParameterEvent data, us.ihmc.idl.CDR cdr)
   {
      read(data, cdr);
   }

   public void copy(rcl_interfaces.msg.dds.ParameterEvent src, rcl_interfaces.msg.dds.ParameterEvent dest)
   {
      staticCopy(src, dest);
   }

   @Override
   public ParameterEventPubSubType newInstance()
   {
      return new ParameterEventPubSubType();
   }
}